package com.example.tasky.agenda.agenda_presentation.components

import kotlinx.serialization.Serializable

@Serializable
enum class AgendaOption(val displayName: String) {
    EVENT("Event"),
    TASK("Task"),
    REMINDER("Reminder")
}