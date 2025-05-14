package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.action.AgendaItemEditAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AgendaItemEditViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaItemEditState())
    val state = _state.asStateFlow()

    fun onAction(action: AgendaItemEditAction) {
        when (action) {
            is AgendaItemEditAction.OnUpdateDescription -> {
                _state.update { it.copy(description = action.description) }
            }
            is AgendaItemEditAction.OnUpdateTitle -> {
                _state.update { it.copy(title = action.title) }
            }
            AgendaItemEditAction.OnNavigateBack -> {}
            AgendaItemEditAction.OnSaveAgendaItem -> {}
        }
    }
}

data class AgendaItemEditState(
    val title: String = "",
    val description: String = ""
)