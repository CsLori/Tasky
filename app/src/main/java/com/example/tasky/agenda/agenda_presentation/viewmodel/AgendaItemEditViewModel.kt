package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgendaItemEditViewModel @Inject constructor(
    agendaRepository: AgendaRepository
) : ViewModel() {

    var state = mutableStateOf(AgendaItemEditState())
        private set

    fun updateState(action: AgendaItemEditUpdate) {
        when (action) {
            is AgendaItemEditUpdate.UpdateDescription -> state.value = state.value.copy(
                description = action.description
            )

            is AgendaItemEditUpdate.UpdateTitle -> state.value = state.value.copy(
                title = action.title
            )
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
    data object OnBackPressed : AgendaItemEditAction
    data object OnSavePressed : AgendaItemEditAction
}