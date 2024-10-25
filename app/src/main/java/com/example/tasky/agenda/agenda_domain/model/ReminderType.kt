package com.example.tasky.agenda.agenda_domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderType {
    TASK,
    REMINDER,
    EVENT,
    NONE
}