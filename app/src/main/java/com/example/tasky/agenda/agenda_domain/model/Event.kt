package com.example.tasky.agenda.agenda_domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String?,
    val description: String?,
    val from: Long?,
    val to: Long?,
    val remindAt: Long?,
    val attendeeIds: List<String>?
)
