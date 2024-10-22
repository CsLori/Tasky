package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.util.Result
import com.example.tasky.util.TaskyError
import com.example.tasky.util.asResult
import com.example.tasky.util.mapToTaskyError
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(private val api: TaskyApi) : AgendaRepository {
    override suspend fun addTask(
        id: String,
        title: String,
        description: String,
        time: Long,
        remindAt: Long,
        isDone: Boolean
    ): Result<Unit, TaskyError> {
        return try {
            api.addTask(
                Task(
                    id = id,
                    title = title,
                    description = description,
                    time = time,
                    remindAt = remindAt,
                    isDone = isDone
                )
            )
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()

            val error = e.asResult(::mapToTaskyError).error

            Result.Error(error)
        }
    }
}