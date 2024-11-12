package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.AgendaResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItems

fun AgendaResponse.toAgendaItems(): AgendaItems {
    return AgendaItems(
        events = events.map { it.toEvent() },
        tasks = tasks.map { it.toTask() },
        reminders = reminders.map { it.toReminder() }
    )
}