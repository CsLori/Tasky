package com.example.tasky.agenda.agenda_domain.model

import java.time.LocalDateTime

data class Attendee(
    val email: String,
    val name: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: LocalDateTime,
    val isCreator: Boolean
)