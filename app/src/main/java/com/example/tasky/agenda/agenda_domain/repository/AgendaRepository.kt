package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.util.Result
import com.example.tasky.util.TaskyError

interface AgendaRepository {
    suspend fun addTask(
        id: String,
        title: String,
        description: String,
        time: Long,
        remindAt: Long,
        isDone: Boolean
    ): Result<Unit, TaskyError>
}