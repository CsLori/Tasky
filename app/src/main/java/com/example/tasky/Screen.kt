package com.example.tasky

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
    object AgendaDetail

    @Serializable
    data class AgendaItemEdit(
        val title: String,
        val description: String?,
        val editType: EditType
    )
}