package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_presentation.ui.agenda.AgendaNavigationEvent
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaUpdateState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val alarmScheduler: AlarmScheduler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _navigationEvents = MutableSharedFlow<AgendaNavigationEvent>()
    val navigationEvents: SharedFlow<AgendaNavigationEvent> = _navigationEvents

    private var _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

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

    fun onAction(action: AgendaAction) {
        when (action) {
            is AgendaAction.DeleteAgendaItem -> deleteAgendaItem(action.agendaItem)
            AgendaAction.Logout -> {
                viewModelScope.launch {
                    logout()
                    _navigationEvents.emit(AgendaNavigationEvent.NavigateToLoginScreen)
                }
            }

            is AgendaAction.EditPressed -> {
                viewModelScope.launch {
                    _navigationEvents.emit(
                        AgendaNavigationEvent.NavigateToAgendaDetailScreen(
                            agendaItemId = action.agendaItem.id,
                            isAgendaItemReadOnly = false,
                            agendaOption = state.value.agendaOption
                        )
                    )
                }
            }
            is AgendaAction.OpenPressed -> {
                viewModelScope.launch {
                    _navigationEvents.emit(
                        AgendaNavigationEvent.NavigateToAgendaDetailScreen(
                            agendaItemId = action.agendaItem.id,
                            isAgendaItemReadOnly = true,
                            agendaOption = state.value.agendaOption
                        )
                    )
                }
            }
            is AgendaAction.FilterAgendaItems -> getAgendaItemsForSelectedDate(action.filterDate.toLocalDateTime())
            is AgendaAction.IsDoneChange -> updateTaskOnIsDoneChange()
            AgendaAction.FabItemPressed -> {
                viewModelScope.launch {
                    _navigationEvents.emit(
                        AgendaNavigationEvent.NavigateToAgendaDetailScreen(
                            agendaItemId = null,
                            isAgendaItemReadOnly = false,
                            agendaOption = state.value.agendaOption
                        )
                    )
                }
            }
        }
    }

    private fun getAgendaItemsForSelectedDate(filterDate: LocalDateTime) {
        selectedDate.value = filterDate
    }

    val syncResult = networkStatus
        .filter { it == NetworkStatus.Connected && state.value.hasDeviceBeenOffline }
        .mapLatest { agendaRepository.syncAgenda() }
        .onEach { result ->
            when (result) {
                is Success -> {
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


    private fun deleteAgendaItem(agendaItem: AgendaItem) {
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

    fun updateState(action: AgendaUpdateState) {
        _state.update {
            when (action) {
                is AgendaUpdateState.UpdateSelectedDate -> it.copy(selectedDate = action.newDate)
                is AgendaUpdateState.UpdateSelectedOption -> it.copy(agendaOption = action.item)
                is AgendaUpdateState.UpdateIsDateSelectedFromDatePicker -> it.copy(
                    isDateSelectedFromDatePicker = action.isDateSelectedFromDatePicker
                )

                is AgendaUpdateState.UpdateMonth -> it.copy(month = action.month)
                is AgendaUpdateState.UpdateSelectedIndex -> it.copy(
                    selectedIndex = action.selectedIndex,
                    isDateSelectedFromDatePicker = false
                )

                is AgendaUpdateState.UpdateSelectedItem -> it.copy(selectedItem = action.agendaItem)
                is AgendaUpdateState.UpdateIsDone -> {
                    it.copy(
                        selectedItem = it.selectedItem?.copy(
                            details = (it.selectedItem.details as? AgendaItemDetails.Task)?.copy(
                                isDone = action.isDone
                            )
                                ?: it.selectedItem.details
                        )
                    )
                }
            }
        }
    }

    private suspend fun logout() {
        _state.update { it.copy(isLoading = true) }
            when (defaultUserRepository.logout()) {
                is Success -> {
                    cancelAlarms()
                }
                is Error -> {}
            }
            _state.update { it.copy(isLoading = false) }
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

    private fun updateTaskOnIsDoneChange() {
        viewModelScope.launch {
            state.value.selectedItem?.let { safeTask ->
                agendaRepository.updateTask(
                    task = safeTask,
                    shouldScheduleAlarm = false
                )
            }
        }
    }


    sealed class ErrorDialogState {
        data object None : ErrorDialogState()
        data class ItemNotFound(val uiText: UiText) : ErrorDialogState()
        data class AgendaItemDeletionError(val uiText: UiText) : ErrorDialogState()
    }
}