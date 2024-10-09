package com.example.tasky.agenda.agenda_domain

import com.example.tasky.agenda.agenda_domain.util.AgendaError
import com.example.tasky.core.util.Result

interface AgendaRepository {
    suspend fun addTask(
        id: String,
        title: String,
        description: String?,
        time: Long,
        remindAt: Long,
        isDone: Boolean
    ): Result<Unit, AgendaError>
}