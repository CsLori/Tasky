package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.local.dto.AgendaOptionDto
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption

fun AgendaOptionDto.toAgendaOption(): AgendaOption = when (displayName) {
    "Event" -> AgendaOption.EVENT
    "Task" -> AgendaOption.TASK
    "Reminder" -> AgendaOption.REMINDER
    else -> throw IllegalArgumentException("Unknown AgendaOptionDto: $displayName")
}

fun AgendaOption.toAgendaOptionDto(): AgendaOptionDto = AgendaOptionDto(displayName)