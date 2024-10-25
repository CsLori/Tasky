package com.example.tasky.agenda.agenda_presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.entity_mappers.toTaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaUpdateState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
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
                    Log.d("DDD items", "$items")
                    _state.update {
                        it.copy(
                            agendaItems = items,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val task = state.value.agendaItems
                .filterIsInstance<AgendaItem.Task>()
                .find { it.id == taskId }

            // Delete task from local database and repository if found
            task?.let {
                localDatabaseRepository.deleteTask(it.toTaskEntity())
                agendaRepository.deleteTask(it)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateState(action: AgendaUpdateState) {
        _state.update {
            when (action) {
                is AgendaUpdateState.UpdateSelectedDate -> it.copy(selectedDate = action.newDate)
                is AgendaUpdateState.UpdateItemSelected -> it.copy(itemSelected = action.item)
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
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = agendaRepository.logout()) {
                is Success -> _uiState.update { AgendaViewModel.AgendaUiState.Success }
                is Error -> _uiState.update { AgendaViewModel.AgendaUiState.None }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    sealed class AgendaUiState {
        data object None : AgendaUiState()
        data object Success : AgendaUiState()
    }
}