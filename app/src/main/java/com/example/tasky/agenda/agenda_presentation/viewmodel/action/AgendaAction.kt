package com.example.tasky.agenda.agenda_presentation.viewmodel.action

sealed interface AgendaAction {
    data object OnDeleteAgendaItem: AgendaAction
}