package com.example.tasky.agenda.agenda_data.remote.dto

import com.example.tasky.agenda.agenda_domain.model.Event
import com.example.tasky.agenda.agenda_domain.model.Reminder
import com.example.tasky.agenda.agenda_domain.model.Task
import kotlinx.serialization.Serializable

@Serializable
data class AgendaResponse(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
