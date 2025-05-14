package com.example.tasky.agenda.agenda_presentation.action

sealed interface AgendaItemEditAction {
    data class OnUpdateDescription(val description: String) : AgendaItemEditAction
    data class OnUpdateTitle(val title: String) : AgendaItemEditAction
    data object OnNavigateBack : AgendaItemEditAction
    data object OnSaveAgendaItem : AgendaItemEditAction

}