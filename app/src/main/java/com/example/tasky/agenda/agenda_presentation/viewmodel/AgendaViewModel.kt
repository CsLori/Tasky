package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_presentation.action.AgendaAction
import com.example.tasky.agenda.agenda_presentation.state.AgendaState
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.util.ConnectivityService
import com.example.tasky.util.NetworkStatus
import com.example.tasky.util.getInitials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val defaultUserRepository: UserRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val networkConnectivityService: ConnectivityService,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaUiState>(AgendaUiState.None)
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _errorDialogState = MutableStateFlow<ErrorDialogState>(ErrorDialogState.None)
    val errorDialogState: StateFlow<ErrorDialogState> = _errorDialogState.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime

    private val networkStatus: StateFlow<NetworkStatus> =
        networkConnectivityService.networkStatus.stateIn(
            initialValue = NetworkStatus.Unknown,
            scope = viewModelScope,
            started = WhileSubscribed(5000)
        )

    private var selectedDate = MutableStateFlow(LocalDateTime.now())

    var userInitials: String = ""

    init {
        viewModelScope.launch {
            userInitials = getInitials(getUserName())
        }
    }

    fun getAgendaItems(filterDate: LocalDateTime) {
        selectedDate.value = filterDate
    }

    val syncResult = networkStatus
        .filter { it == NetworkStatus.Connected && state.value.hasDeviceBeenOffline }
        .mapLatest { agendaRepository.syncAgenda() }
        .onEach { result ->
            when (result) {
                is Success -> {
                    _uiState.update { AgendaUiState.None }
                    _state.update { it.copy(hasDeviceBeenOffline = false) }
                }

                is Error -> {
                    _errorDialogState.update {
                        ErrorDialogState.AgendaItemDeletionError(
                            UiText.StringResource(R.string.Could_not_sync_agenda_items)
                        )
                    }
                }
            }
        }
        .stateIn(
            viewModelScope,
            WhileSubscribed(5_000L),
            Success(NetworkStatus.Unknown)
        )

    val agendaItemsForSelectedDate = selectedDate
        .onEach { _state.update { it.copy(isLoading = true) } }
        .flatMapLatest { date ->
            when (val result = agendaRepository.getAllAgendaItemsForDate(date)) {
                is Success -> result.data
                is Error -> emptyFlow()
            }.onEach { agendaItems ->
                _state.update { agendaState ->
                    agendaState.copy(
                        agendaItems = agendaItems,
                        isLoading = false
                    )
                }
            }
        }.stateIn(viewModelScope, WhileSubscribed(5_000L), emptyList())

    val needlePosition: StateFlow<Int> = combine(agendaItemsForSelectedDate, currentTime) { items, time ->
        items.indexOfFirst { it.time.toLong() >= time }
            .takeIf { it != -1 } ?: items.size
    }.stateIn(viewModelScope,  WhileSubscribed(5_000L), 0)

    val shouldShowNeedle: StateFlow<Boolean> = agendaItemsForSelectedDate.map { items ->
        items.any { it.time.toLong() <= System.currentTimeMillis() }
    }.stateIn(viewModelScope,  WhileSubscribed(5_000L), false)

    fun refreshTime() {
        _currentTime.value = System.currentTimeMillis()
    }


    fun deleteAgendaItem(agendaItem: AgendaItem) {
        viewModelScope.launch {
            val existingAgendaItem =
                state.value.agendaItems.find { it.id == agendaItem.id }

            existingAgendaItem?.let { safeAgendaItem ->
                when (agendaItem.details) {
                    is AgendaItemDetails.Task -> {
                        val task = safeAgendaItem as? AgendaItem
                        if (task != null) {
                            agendaRepository.deleteTask(task.id)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }

                    }

                    is AgendaItemDetails.Event -> {
                        val event = safeAgendaItem as? AgendaItem
                        if (event != null) {
                            agendaRepository.deleteEvent(event.id)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }
                    }

                    is AgendaItemDetails.Reminder -> {
                        val reminder = safeAgendaItem as? AgendaItem
                        if (reminder != null) {
                            agendaRepository.deleteReminder(reminder.id)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }
                    }
                }
            }
            handleAgendaItemNotFound()
        }
    }

    private fun handleAgendaItemNotFound() {
        _dialogState.update { DialogState.ShowError }
        _errorDialogState.update {
            ErrorDialogState.AgendaItemDeletionError(
                UiText.StringResource(
                    R.string.Item_not_found
                )
            )
        }
    }

    private fun handleItemMismatch() {
        _dialogState.update { DialogState.ShowError }
        _errorDialogState.update {
            ErrorDialogState.ItemNotFound(
                UiText.StringResource(
                    R.string.Could_not_delete_agenda_item
                )
            )
        }
    }

    fun logout() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (defaultUserRepository.logout()) {
                is Success -> {
                    cancelAlarms()
                    _uiState.update { AgendaUiState.None }
                }

                is Error -> {}
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun cancelAlarms() {
        viewModelScope.launch {
            localDatabaseRepository.getAllAgendaItems().mapLatest { agendaItems ->
                agendaItems.forEach { agendaItem ->
                    alarmScheduler.cancel(agendaItem)
                }
            }
        }
    }

    fun showErrorDialog() {
        _dialogState.update { DialogState.ShowError }
    }

    private suspend fun getUserName(): String {
        val userNameDeferred = viewModelScope.async {
            userPrefsRepository.getUserName()
        }
        return userNameDeferred.await()
    }

    fun getNumberOfDeletedItemsForSync(): Int? {
        var numberOfSyncedItems = 0
        viewModelScope.launch {
            numberOfSyncedItems = localDatabaseRepository.getDeletedItemsForSync().size
        }
        return numberOfSyncedItems
    }

    fun updateTaskOnIsDoneChange() {
        viewModelScope.launch {
            state.value.selectedItem?.let { safeTask ->
                agendaRepository.updateTask(
                    task = safeTask,
                    shouldScheduleAlarm = false
                )
            }
        }
    }

    fun onAction(action: AgendaAction) {
        when (action) {
            is AgendaAction.OnDeleteAgendaItem -> deleteAgendaItem(action.agendaItem)
            is AgendaAction.OnLogout -> logout()
            is AgendaAction.OnFabItemPressed -> { /* Handle in UI */ }
            is AgendaAction.OnOpenPressed -> { /* Handle in UI */ }
            is AgendaAction.OnFilterAgendaItems -> getAgendaItems(action.filterDate.toLocalDateTime())
            is AgendaAction.OnIsDoneChange -> updateTaskOnIsDoneChange()
            
            // Handle new actions that were previously state updates
            is AgendaAction.OnDateSelected -> {
                _state.update { it.copy(selectedDate = action.newDate) }
            }
            is AgendaAction.OnAgendaOptionSelected -> {
                _state.update { it.copy(agendaOption = action.option) }
            }
            is AgendaAction.OnMonthChanged -> {
                _state.update { it.copy(month = action.month) }
            }
            is AgendaAction.OnDatePickerSelection -> {
                _state.update { it.copy(isDateSelectedFromDatePicker = action.isDateSelectedFromDatePicker) }
            }
            is AgendaAction.OnDayIndexSelected -> {
                _state.update { it.copy(selectedIndex = action.selectedIndex) }
                _state.update { it.copy(month = action.date.toLocalDateTime().month.name) }
                getAgendaItems(action.date.toLocalDateTime())
            }
            is AgendaAction.OnAgendaItemSelected -> {
                _state.update { it.copy(selectedItem = action.agendaItem) }
            }
            is AgendaAction.OnTaskCompletionChanged -> {
                _state.update { 
                    it.copy(
                        selectedItem = action.agendaItem,
                        agendaItems = it.agendaItems.map { item ->
                            if (item.id == action.agendaItem.id) {
                                item.copy(
                                    details = (item.details as? AgendaItemDetails.Task)?.copy(
                                        isDone = action.isDone
                                    ) ?: item.details
                                )
                            } else item
                        }
                    )
                }
                updateTaskOnIsDoneChange()
            }
        }
    }

    sealed class ErrorDialogState {
        data object None : ErrorDialogState()
        data class ItemNotFound(val uiText: UiText) : ErrorDialogState()
        data class AgendaItemDeletionError(val uiText: UiText) : ErrorDialogState()
    }

    sealed class AgendaUiState {
        data object None : AgendaUiState()
        data object Success : AgendaUiState()
    }
}