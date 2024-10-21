package com.example.tasky.agenda.agenda_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.agenda.agenda_domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        time = this.time,
        remindAt = this.remindAt,
        isDone = this.isDone
    )
}
