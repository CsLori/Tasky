package com.example.tasky

import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import kotlinx.serialization.Serializable

object Screen {

    @Serializable
    object Login

    @Serializable
    object Register

    @Serializable
    object Agenda

    @Serializable
    data class AgendaDetail(
        val agendaItemId: String? = null,
        val isAgendaItemReadOnly: Boolean,
        val agendaOption: AgendaOption? = null
    )

    @Serializable
    data class AgendaItemEdit(
        val title: String,
        val description: String? = null,
        val editType: EditType
    )
}