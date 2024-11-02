package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReminderSerialized(
    val reminderId: String,
    val reminderTitle: String,
    val reminderDescription: String?,
    val time: Long,
    val remindAtTime: Long,
)