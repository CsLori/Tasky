package com.example.tasky.agenda.agenda_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long
)