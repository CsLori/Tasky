package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeMinimalDto(
    val email: String,
    val userId: String,
    val fullName: String,
)