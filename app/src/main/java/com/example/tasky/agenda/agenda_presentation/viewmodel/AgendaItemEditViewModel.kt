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
                is AgendaItemEditUpdate.UpdateDescription -> it.copy(
                    description = action.description
                )

                is AgendaItemEditUpdate.UpdateTitle -> it.copy(
                    title = action.title
                )
            }
        }
    }
}

sealed interface AgendaItemEditUpdate {
    data class UpdateDescription(val description: String) : AgendaItemEditUpdate
    data class UpdateTitle(val title: String) : AgendaItemEditUpdate
}

data class AgendaItemEditState(
    val title: String = "",
    val description: String = ""
)

sealed interface AgendaItemEditAction {
    data object OnBackPressed: AgendaItemEditAction
    data object OnSavePressed: AgendaItemEditAction
}