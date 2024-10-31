@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.presentation.DateUtils.localDateToStringMMMdyyyyFormat
import com.example.tasky.core.presentation.DateUtils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaDetailState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaDetailUiState>(AgendaDetailUiState.None)
    val uiState: StateFlow<AgendaDetailUiState> = _uiState.asStateFlow()

    val agendaOption = savedStateHandle.get<AgendaOption>("agendaOption") ?: AgendaOption.EVENT
    private val isReadOnly = savedStateHandle.toRoute<Screen.AgendaDetail>().isAgendaItemReadOnly

    init {
        updateState(AgendaDetailStateUpdate.UpdateIsReadOnly(isReadOnly))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun updateState(action: AgendaDetailStateUpdate) {
        _state.update {
            when (action) {
                is AgendaDetailStateUpdate.UpdateDate -> it.copy(
                    date = action.newDate.localDateToStringMMMdyyyyFormat(),
                    isDateSelectedFromDatePicker = false
                )

                is AgendaDetailStateUpdate.UpdateTime -> {
                    val updateTime = LocalTime.of(action.hour, action.minute).toMillis()
                    it.copy(task = it.task.copy(time = updateTime))
                }

                is AgendaDetailStateUpdate.UpdateShouldShowDatePicker -> it.copy(
                    shouldShowDatePicker = action.shouldShowDatePicker
                )

                is AgendaDetailStateUpdate.UpdateMonth -> it.copy(month = action.month)
                is AgendaDetailStateUpdate.UpdateShouldShowTimePicker -> it.copy(
                    shouldShowTimePicker = action.shouldShowTimePicker
                )

                is AgendaDetailStateUpdate.UpdateEditType -> it.copy(editType = action.editType)
                is AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown -> it.copy(
                    shouldShowReminderDropdown = action.shouldShowReminderDropdown
                )

                is AgendaDetailStateUpdate.UpdateSelectedReminder -> it.copy(
                    selectedReminder = action.selectedReminder
                )

                is AgendaDetailStateUpdate.UpdateDescription -> it.copy(
                    task = it.task.copy(
                        taskDescription = action.description
                    )
                )

                is AgendaDetailStateUpdate.UpdateTitle -> it.copy(task = it.task.copy(taskTitle = action.title))
                is AgendaDetailStateUpdate.UpdateIsReadOnly -> it.copy(isReadOnly = action.isReadOnly)
                is AgendaDetailStateUpdate.UpdateSelectedAgendaItem -> it.copy(selectedAgendaItem = action.selectedAgendaItem)
            }
        }
    }

    fun createTask() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result = agendaRepository.addTask(
                task = state.value.task
            )
            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                    Success(Unit)
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                    Error(result.error)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateTask(task: AgendaItem.Task) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = agendaRepository.updateTask(task)

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                    Success(Unit)
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                    Error(result.error)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun loadTask(taskId: String): AgendaItem {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            val taskEntity = localDatabaseRepository.getTaskById(taskId)

            _state.update { currentState ->
                currentState.copy(
                    task = taskEntity.toAgendaItem(),
                    isLoading = false
                )
            }
        }
        return state.value.task
    }

    sealed class AgendaDetailUiState {
        data object None : AgendaDetailUiState()
        data object Success : AgendaDetailUiState()
    }
}