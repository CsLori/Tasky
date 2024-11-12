package com.example.tasky.agenda.agenda_domain.model

data class AgendaItems (
    val events: List<AgendaItem.Event>,
    val tasks: List<AgendaItem.Task>,
    val reminders: List<AgendaItem.Reminder>
)