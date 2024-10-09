package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_data.TaskBody
import com.example.tasky.agenda.agenda_domain.AgendaRepository
import com.example.tasky.agenda.agenda_domain.util.AgendaError
import com.example.tasky.core.remote.TaskyApi
import com.example.tasky.core.util.Result
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(private val api: TaskyApi) : AgendaRepository {
    override suspend fun addTask(
        id: String,
        title: String,
        description: String?,
        time: Long,
        remindAt: Long,
        isDone: Boolean
    ): Result<Unit, AgendaError> {
        return try {
            api.addTask(
                TaskBody(
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
            val error = when (e) {
                is UnknownHostException -> AgendaError.General.NO_INTERNET
                is HttpException -> when (e.code()) {
                    401 -> AgendaError.General.UNAUTHORIZED
                    404 -> AgendaError.General.NOT_FOUND
                    else -> AgendaError.General.SERVER_ERROR
                }
                else -> AgendaError.General.GENERAL_ERROR
            }
            Result.Error(error)
        }
    }
}