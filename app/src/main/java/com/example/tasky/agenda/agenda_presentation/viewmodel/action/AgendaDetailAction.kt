package com.example.tasky.agenda.agenda_presentation.viewmodel.action

sealed interface AgendaDetailAction {
    data object OnClosePressed : AgendaDetailAction
    data object OnSavePressed : AgendaDetailAction
    data object OnCreateSuccess : AgendaDetailAction
    data object OnEditField : AgendaDetailAction
}