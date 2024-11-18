package com.example.tasky.agenda.agenda_domain

import com.example.tasky.agenda.agenda_domain.model.AgendaItem

interface NotificationScheduler {
    fun schedule(agendaItem: AgendaItem)
    fun cancel(agendaItem: AgendaItem)
}