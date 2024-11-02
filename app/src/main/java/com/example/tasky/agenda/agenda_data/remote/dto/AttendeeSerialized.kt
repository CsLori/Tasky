package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeSerialized(
    val userId: String,
    val email: String,
    val name: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long,
    val isCreator: Boolean,
)