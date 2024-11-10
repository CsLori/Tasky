package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskSerialized(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val isDone: Boolean,
    val remindAt: Long,
)