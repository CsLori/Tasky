package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.util.Result
import com.example.tasky.util.TaskyError

interface AgendaRepository {
    suspend fun addTask(task: Task): Result<Unit, TaskyError>

    suspend fun deleteTask(task: Task): Result<Unit, TaskyError>
}