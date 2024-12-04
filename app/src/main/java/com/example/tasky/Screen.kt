package com.example.tasky

import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import kotlinx.serialization.Serializable

object Screen {

    @Serializable
    object Login

    @Serializable
    object Register {
        const val route = "register"
    }

    @Serializable
    object Agenda

    @Serializable
    data class AgendaDetail(
        val agendaItemId: String? = null,
        val agendaOption: AgendaOption,
        val isAgendaItemReadOnly: Boolean = false,
        val photoId: String? = null
    )

    @Serializable
    data class AgendaItemEdit(
        val title: String,
        val description: String? = null,
        val editType: EditType
    )

    @Serializable
    data class Photo(val photoKey: String, val photoUrl: String)
}