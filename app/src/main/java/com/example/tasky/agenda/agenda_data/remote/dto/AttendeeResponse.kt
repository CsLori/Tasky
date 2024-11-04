package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeSerialized
)