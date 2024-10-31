package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderTypeSerialized {
    TASK,
    REMINDER,
    EVENT,
    NONE
}