package com.example.tasky.agenda.agenda_domain.model

data class Attendee(
    val email: String,
    val name: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean = true,
    val remindAt: Long,
    val isCreator: Boolean
)