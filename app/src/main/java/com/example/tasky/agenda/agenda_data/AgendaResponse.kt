package com.example.tasky.agenda.agenda_data

import kotlinx.serialization.Serializable

@Serializable
data class AgendaResponse(
    val events: List<Event>,
    val tasks: List<TaskBody>,
    val reminders: List<Reminder>
)
