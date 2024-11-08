package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaUpdateState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val defaultUserRepository: DefaultUserRepository,
    private val localDatabaseRepository: LocalDatabaseRepository
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaUiState>(AgendaUiState.None)
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    init {
        getAgendaItems()
    }

    private fun getAgendaItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            localDatabaseRepository.getAllAgendaItems()
                .collect { items ->
                    _state.update {
                        it.copy(
                            agendaItems = items,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val existingAgendaItem = state.value.agendaItems.find { it.id == agendaItem.id }

            existingAgendaItem?.let { safeAgendaItem ->
                when (agendaItem) {
                    is AgendaItem.Task -> {
                        val task = safeAgendaItem as AgendaItem.Task
                        agendaRepository.deleteTask(task)
                    }

                    is AgendaItem.Event -> {
                        val event = safeAgendaItem as AgendaItem.Event
                        agendaRepository.deleteEvent(event)
                    }

                    is AgendaItem.Reminder -> {
                        val reminder = safeAgendaItem as AgendaItem.Reminder
                        agendaRepository.deleteReminder(reminder)
                    }
                }
                _state.update { it.copy(isLoading = false) }
            }
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
                is AgendaUpdateState.UpdateShouldShowDatePicker -> it.copy(shouldShowDatePicker = action.shouldShowDatePicker)
                is AgendaUpdateState.UpdateSelectedIndex -> it.copy(
                    selectedIndex = action.selectedIndex,
                    isDateSelectedFromDatePicker = false
                )

                is AgendaUpdateState.UpdateSelectedItem -> it.copy(selectedItem = action.agendaItem)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (defaultUserRepository.logout()) {
                is Success -> _uiState.update { AgendaUiState.Success }
                is Error -> _uiState.update { AgendaUiState.None }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    sealed class AgendaUiState {
        data object None : AgendaUiState()
        data object Success : AgendaUiState()
    }
}