package com.example.tasky.agenda.agenda_presentation.viewmodel.action

sealed interface AgendaAction {
    data class OnDeleteAgendaItem(val taskId: String): AgendaAction
    data object OnLogout: AgendaAction
}