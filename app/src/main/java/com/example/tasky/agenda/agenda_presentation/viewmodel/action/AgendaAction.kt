package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import com.example.tasky.agenda.agenda_domain.model.AgendaItem

sealed interface AgendaAction {
    data class DeleteAgendaItem(val agendaItem: AgendaItem) : AgendaAction
    data object Logout : AgendaAction
    data class EditPressed(val agendaItem: AgendaItem) : AgendaAction
    data object FabItemPressed : AgendaAction
    data class OpenPressed(val agendaItem: AgendaItem) : AgendaAction
    data class FilterAgendaItems(val filterDate: Long) : AgendaAction
    data object IsDoneChange : AgendaAction
}