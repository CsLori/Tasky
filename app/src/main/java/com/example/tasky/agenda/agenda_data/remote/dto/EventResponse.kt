package com.example.tasky.agenda.agenda_data.remote.dto

import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<Attendee>,
    val photos: List<Photo>
)