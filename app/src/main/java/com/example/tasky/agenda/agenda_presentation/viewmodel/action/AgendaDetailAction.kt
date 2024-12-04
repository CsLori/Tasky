package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import android.net.Uri
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.Attendee

sealed interface AgendaDetailAction {
    data object OnClosePressed : AgendaDetailAction
    data object OnSavePressed : AgendaDetailAction
    data object OnCreateSuccess : AgendaDetailAction
    data object OnEnableEditPressed : AgendaDetailAction
    data object OnEditRowPressed : AgendaDetailAction
    data object OnAddVisitorPressed : AgendaDetailAction
    data object OnVisitorFilterChanged : AgendaDetailAction
    data class OnPhotoCompress(val uri: Uri) : AgendaDetailAction
    data class OnPhotoPressed(val key: String) : AgendaDetailAction
    data class OnDeleteAgendaItem(val agendaItem: AgendaItem) : AgendaDetailAction
    data class OnDeleteAttendee(val attendee: Attendee) : AgendaDetailAction
    data class OnNotificationPromptSeen(val hasSeenNotificationPrompt: Boolean) : AgendaDetailAction
}