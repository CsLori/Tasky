package com.example.tasky.agenda.agenda_domain

import com.example.tasky.agenda.agenda_domain.model.AgendaItem

interface AlarmScheduler {
    suspend fun schedule(agendaItem: AgendaItem)
    suspend fun cancel(agendaItem: AgendaItem)
}