@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.util.DateUtils.localDateToStringMMMdyyyyFormat
import com.example.tasky.util.DateUtils.toMillis
import com.example.tasky.util.Result.Error
import com.example.tasky.util.Result.Success
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
    private val agendaRepository: AgendaRepository
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

    sealed class AgendaDetailUiState {
        data object None : AgendaDetailUiState()
        data object Success : AgendaDetailUiState()
    }
}
