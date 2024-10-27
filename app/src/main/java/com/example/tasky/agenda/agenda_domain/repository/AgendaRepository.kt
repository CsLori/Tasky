package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError

interface AgendaRepository {
    suspend fun addTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun updateTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun deleteTask(task: AgendaItem.Task): Result<Unit, TaskyError>

}