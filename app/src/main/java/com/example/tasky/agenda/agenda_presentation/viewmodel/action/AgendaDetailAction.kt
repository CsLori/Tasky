package com.example.tasky.agenda.agenda_presentation.viewmodel.action

sealed interface AgendaDetailAction {
    data object OnClosePress : AgendaDetailAction
    data object OnSavePress : AgendaDetailAction
    data object OnCreateSuccess : AgendaDetailAction
    data object OnEditField : AgendaDetailAction
    data object OnReminderPress : AgendaDetailAction
}