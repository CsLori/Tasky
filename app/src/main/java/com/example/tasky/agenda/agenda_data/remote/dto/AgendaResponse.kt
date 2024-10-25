package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AgendaResponse(
    val events: List<EventResponse>,
    val tasks: List<TaskResponse>,
    val reminders: List<ReminderResponse>
)