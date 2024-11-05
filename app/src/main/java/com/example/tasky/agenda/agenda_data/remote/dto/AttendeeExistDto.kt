package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeExistDto(
    val doesUserExist: Boolean,
    val attendee: AttendeeMinimalDto
)