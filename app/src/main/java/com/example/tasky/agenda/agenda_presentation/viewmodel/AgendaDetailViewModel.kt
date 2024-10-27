@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.entity_mappers.toTaskEntity
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
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
    private val localDatabaseRepository: LocalDatabaseRepository
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaDetailState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaDetailUiState>(AgendaDetailUiState.None)
    val uiState: StateFlow<AgendaDetailUiState> = _uiState.asStateFlow()

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

                is AgendaDetailStateUpdate.UpdateDescription -> it.copy(task = it.task.copy(taskDescription = action.description))
                is AgendaDetailStateUpdate.UpdateTitle -> it.copy(task = it.task.copy(taskTitle = action.title))
                is AgendaDetailStateUpdate.UpdateIsReadOnly -> it.copy(isReadOnly = action.isReadOnly)
            }
        }
    }

    fun createTask() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            try {
                val task = state.value.task
                localDatabaseRepository.insertTask(task.toTaskEntity())
            } catch (e: Exception) {
                e.printStackTrace()

                _state.update {
                    it.copy(isLoading = false)
                }
            }

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
            try {
                localDatabaseRepository.updateTask(task.toTaskEntity())
                agendaRepository.updateTask(task)
            } catch (e: Exception) {
                e.printStackTrace()

                _state.update {
                    it.copy(isLoading = false)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getTaskById(taskId: String): TaskEntity? {
        return try {
            localDatabaseRepository.getTaskById(taskId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val taskEntity = getTaskById(taskId)
            Log.d("DDD - getTaskById", "$taskEntity")

            if (taskEntity != null) {

                _state.update { currentState ->
                    currentState.copy(
                        task = taskEntity.toAgendaItem(),
                        isLoading = false
                    )
                }
                Log.d("DDD - state.task", "${state.value.task}")
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    sealed class AgendaDetailUiState {
        data object None : AgendaDetailUiState()
        data object Success : AgendaDetailUiState()
    }
}