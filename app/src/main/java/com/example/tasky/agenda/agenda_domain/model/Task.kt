package com.example.tasky.agenda.agenda_domain.model

import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean,
)

fun TaskEntity.toEntity(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description,
        time = this.time,
        remindAt = this.remindAt,
        isDone = this.isDone
    )
}
