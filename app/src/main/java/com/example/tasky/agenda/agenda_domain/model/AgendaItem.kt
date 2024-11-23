package com.example.tasky.agenda.agenda_domain.model

import java.time.LocalDateTime

data class AgendaItem(
    val id: String,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val remindAt: LocalDateTime,
    val details: AgendaItemDetails,
)

sealed interface AgendaItemDetails {
    data class Event(
        val toTime: LocalDateTime,
        val attendees: List<Attendee>,
        val photos: List<Photo>,
        val isUserEventCreator: Boolean,
        val host: String
    ): AgendaItemDetails

    data class Task(
        val isDone: Boolean
    ): AgendaItemDetails

    data object Reminder: AgendaItemDetails
}