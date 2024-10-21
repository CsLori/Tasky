package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AgendaItemEditViewModel @Inject constructor(
    agendaRepository: AgendaRepository
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaItemEditState())
    val state = _state.asStateFlow()

    fun updateState(action: AgendaItemEditUpdate) {
        _state.update {
            when (action) {
                is AgendaItemEditUpdate.UpdateEditField -> it.copy(
                    textFieldState = action.fieldValue
                )
            }
        }
    }

}

sealed interface AgendaItemEditUpdate {
    data class UpdateEditField(val fieldValue: String) : AgendaItemEditUpdate
}

data class AgendaItemEditState(
    val textFieldState: String? = ""
)
