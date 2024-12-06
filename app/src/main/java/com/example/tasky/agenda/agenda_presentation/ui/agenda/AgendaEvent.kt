package com.example.tasky.agenda.agenda_presentation.ui.agenda

import com.example.tasky.agenda.agenda_domain.model.AgendaOption

sealed class AgendaNavigationEvent {
   data object NavigateToLoginScreen: AgendaNavigationEvent()
   data class NavigateToAgendaDetailScreen(
      val agendaItemId: String?,
      val isAgendaItemReadOnly: Boolean,
      val agendaOption: AgendaOption
   ) : AgendaNavigationEvent()
}