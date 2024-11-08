package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import com.example.tasky.agenda.agenda_domain.model.AgendaItem

sealed interface AgendaAction {
    data class OnDeleteAgendaItem(val agendaItem: AgendaItem): AgendaAction
    data object OnLogout: AgendaAction
    data object OnFabItemPressed: AgendaAction
    data class OnOpenPressed(val agendaItem: AgendaItem): AgendaAction
}