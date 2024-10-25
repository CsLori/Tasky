package com.example.tasky.agenda.agenda_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Attendee(
    val email: String = "",
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)