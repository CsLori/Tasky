package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: String,
    val eventTitle: String,
    val eventDescription: String?,
    val from: Long,
    val to: Long,
    val photos: List<PhotoSerialized>,
    val attendees: List<AttendeeSerialized>,
    val isUserEventCreator: Boolean,
    val host: String?,
    val remindAtTime: Long,
)