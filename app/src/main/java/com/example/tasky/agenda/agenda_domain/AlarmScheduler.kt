package com.example.tasky.agenda.agenda_domain

import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption

interface AlarmScheduler {
    suspend fun schedule(agendaItem: AgendaItem, option: AgendaOption)
    suspend fun cancel(agendaItem: AgendaItem)
}