package com.example.tasky.agenda.agenda_data

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: String?,
    val title: String?,
    val description: String?,
    val time: Long?,
    val remindAt: Long?,
)