package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AgendaItemsSerialized(
    val events: List<EventResponse>,
    val tasks: List<TaskSerialized>,
    val reminders: List<ReminderSerialized>
)