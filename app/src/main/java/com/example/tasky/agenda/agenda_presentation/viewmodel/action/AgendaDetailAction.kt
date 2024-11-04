package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import android.net.Uri

sealed interface AgendaDetailAction {
    data object OnClosePressed : AgendaDetailAction
    data object OnSavePressed : AgendaDetailAction
    data object OnCreateSuccess : AgendaDetailAction
    data object OnEnableEditPressed : AgendaDetailAction
    data object OnEditRowPressed : AgendaDetailAction
    data object OnAddVisitorPressed : AgendaDetailAction
    data class OnPhotoCompress(val uri: Uri) : AgendaDetailAction
    data class OnPhotoPressed(val key: String) : AgendaDetailAction
}