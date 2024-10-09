package com.example.tasky.agenda.agenda_data

import kotlinx.serialization.Serializable

@Serializable
data class TaskBody(
    val id: String?,
    val title: String?,
    val description: String?,
    val time: Long?,
    val remindAt: Long?,
    val isDone: Boolean?,
)
