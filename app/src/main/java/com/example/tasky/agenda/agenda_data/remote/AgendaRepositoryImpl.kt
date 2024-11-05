package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_data.createMultipartEventRequest
import com.example.tasky.agenda.agenda_data.createPhotoPart
import com.example.tasky.agenda.agenda_data.dto_mappers.toSerializedTask
import com.example.tasky.agenda.agenda_data.entity_mappers.toEventEntity
import com.example.tasky.agenda.agenda_data.entity_mappers.toTaskEntity
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(
    private val api: TaskyApi,
    private val userPrefsRepository: ProtoUserPrefsRepository,
    private val localDatabaseRepository: LocalDatabaseRepository
) : AgendaRepository {
    override suspend fun addTask(
        task: AgendaItem.Task
    ): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertTask(task.toTaskEntity())
            api.addTask(task.toSerializedTask())
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

    override suspend fun updateTask(task: AgendaItem.Task): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertTask(task.toTaskEntity())
            api.updateTask(task.toSerializedTask())
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


    override suspend fun deleteTask(task: AgendaItem.Task): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteTask(task.toTaskEntity())
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

    override suspend fun addEvent(
        event: AgendaItem.Event,
        photos: List<ByteArray>
    ): Result<EventResponse, TaskyError> {
        val eventPart = createMultipartEventRequest(event)
        val photosPart = photos.mapIndexed() { index, photo -> createPhotoPart(photo, index) }

        return try {
            localDatabaseRepository.upsertEvent(event.toEventEntity())
            api.addEvent(eventPart, photos = photosPart)

        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()

            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getAttendee(email: String): Result<AttendeeExistDto, TaskyError> {
        return try {
            val attendee = api.getAttendee(email)
            Result.Success(attendee)
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