package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskSerialized(
    val taskId: String,
    val taskTitle: String,
    val taskDescription: String?,
    val time: Long,
    val isDone: Boolean,
    val remindAtTime: Long,
)