package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.util.Result
import com.example.tasky.util.TaskyError
import com.example.tasky.util.asResult
import com.example.tasky.util.mapToTaskyError
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(
    private val api: TaskyApi,
    private val userPrefsRepository: ProtoUserPrefsRepository
) : AgendaRepository {
    override suspend fun addTask(
        task: Task
    ): Result<Unit, TaskyError> {
        return try {
            api.addTask(task)
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

    override suspend fun deleteTask(task: Task): Result<Unit, TaskyError> {
        return try {
            api.deleteTaskById(task.id)
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

    override suspend fun logout(): Result<Unit, TaskyError> {
        return try {
            api.logout()
            userPrefsRepository.updateAccessToken("")
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