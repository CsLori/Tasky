package com.example.tasky.agenda.agenda_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String?,
    val title: String?,
    val description: String?,
    val time: Long?,
    val remindAt: Long?,
    val isDone: Boolean?,
)
