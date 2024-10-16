package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.util.AgendaError
import com.example.tasky.util.Result

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