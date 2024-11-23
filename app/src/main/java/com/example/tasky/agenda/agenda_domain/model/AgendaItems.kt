package com.example.tasky.agenda.agenda_domain.model

data class AgendaItems (
    val events: List<AgendaItem>,
    val tasks: List<AgendaItem>,
    val reminders: List<AgendaItem>
)