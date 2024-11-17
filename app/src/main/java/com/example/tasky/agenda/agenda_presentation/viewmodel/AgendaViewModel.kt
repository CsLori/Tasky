package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaUpdateState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import com.example.tasky.util.NetworkConnectivityService
import com.example.tasky.util.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val defaultUserRepository: DefaultUserRepository,
    private val networkConnectivityService: NetworkConnectivityService
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaUiState>(AgendaUiState.None)
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _errorDialogState = MutableStateFlow<ErrorDialogState>(ErrorDialogState.None)
    val errorDialogState: StateFlow<ErrorDialogState> = _errorDialogState.asStateFlow()

    private val networkStatus: StateFlow<NetworkStatus> =
        networkConnectivityService.networkStatus.stateIn(
            initialValue = NetworkStatus.Unknown,
            scope = viewModelScope,
            started = WhileSubscribed(5000)
        )

    private var selectedDate = MutableStateFlow(LocalDate.now())

    fun getAgendaItems(filterDate: LocalDate) {
        selectedDate.value = filterDate
    }

    val syncResult = networkStatus
        .filter { it == NetworkStatus.Connected }
        .mapLatest { agendaRepository.syncAgenda() }
        .onEach { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.update { AgendaUiState.None }
                }

                is Result.Error -> {
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
            SharingStarted.WhileSubscribed(5_000L),
            Result.Success(NetworkStatus.Unknown)
        )

    val agendaItems = selectedDate
        .onEach { _state.update { it.copy(isLoading = true) } }
        .flatMapLatest { date ->
            when (val result = agendaRepository.getAllAgendaItems(date)) {
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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val existingAgendaItem =
                state.value.agendaItems.find { it.id == agendaItem.id }

            existingAgendaItem?.let { safeAgendaItem ->
                when (agendaItem) {
                    is AgendaItem.Task -> {
                        val task = safeAgendaItem as? AgendaItem.Task
                        if (task != null) {
                            agendaRepository.deleteTask(task)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }

                    }

                    is AgendaItem.Event -> {
                        val event = safeAgendaItem as? AgendaItem.Event
                        if (event != null) {
                            agendaRepository.deleteEvent(event)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }
                    }

                    is AgendaItem.Reminder -> {
                        val reminder = safeAgendaItem as? AgendaItem.Reminder
                        if (reminder != null) {
                            agendaRepository.deleteReminder(reminder)

                        } else {
                            handleItemMismatch()
                            return@launch
                        }
                    }
                }
            }
            handleAgendaItemNotFound()
            _state.update { it.copy(isLoading = false) }
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
                is AgendaUpdateState.UpdateVisibility -> it.copy(isVisible = action.visible)
                is AgendaUpdateState.UpdateIsDateSelectedFromDatePicker -> it.copy(
                    isDateSelectedFromDatePicker = action.isDateSelectedFromDatePicker
                )

                is AgendaUpdateState.UpdateMonth -> it.copy(month = action.month)
                is AgendaUpdateState.UpdateShouldShowDatePicker -> it.copy(
                    shouldShowDatePicker = action.shouldShowDatePicker
                )

                is AgendaUpdateState.UpdateSelectedIndex -> it.copy(
                    selectedIndex = action.selectedIndex,
                    isDateSelectedFromDatePicker = false
                )

                is AgendaUpdateState.UpdateSelectedItem -> it.copy(selectedItem = action.agendaItem)
                is AgendaUpdateState.UpdateIsDone -> {
                    if (it.selectedItem is AgendaItem.Task) {
                        val updatedTask = (it.selectedItem as AgendaItem.Task).copy(isDone = action.isDone)
                        it.copy(
                            selectedItem = updatedTask,
                            agendaItems = it.agendaItems.map { item ->
                                if (item.id == updatedTask.id) updatedTask else item
                            }
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun logout() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (defaultUserRepository.logout()) {
                is Success -> _uiState.update { AgendaUiState.Success }
                is Error -> _uiState.update { AgendaUiState.None }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun showErrorDialog() {
        _dialogState.update { DialogState.ShowError }
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