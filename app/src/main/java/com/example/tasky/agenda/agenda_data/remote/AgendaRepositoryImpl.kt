package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_data.createMultipartEventRequest
import com.example.tasky.agenda.agenda_data.createPhotoPart
import com.example.tasky.agenda.agenda_data.dto_mappers.toEventRequest
import com.example.tasky.agenda.agenda_data.dto_mappers.toEventUpdate
import com.example.tasky.agenda.agenda_data.dto_mappers.toSerializedReminder
import com.example.tasky.agenda.agenda_data.dto_mappers.toSerializedTask
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.entity_mappers.toEventEntity
import com.example.tasky.agenda.agenda_data.entity_mappers.toReminderEntity
import com.example.tasky.agenda.agenda_data.entity_mappers.toTaskEntity
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItems
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
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
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getTaskById(taskId: String): Result<AgendaItem.Task, TaskyError> {
        return try {
            val result = localDatabaseRepository.getTaskById(taskId).toAgendaItem()
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

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
            if (e is CancellationException) throw e

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
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun addEvent(
        event: AgendaItem.Event,
        photos: List<ByteArray>
    ): Result<EventResponse, TaskyError> {
        val eventPart = createMultipartEventRequest(event.toEventRequest())
        val photosPart = photos.mapIndexed { index, photo -> createPhotoPart(photo, index) }

        return try {
            localDatabaseRepository.upsertEvent(event.toEventEntity())
            val result = api.addEvent(eventPart, photosPart)

            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getEventById(eventId: String): Result<AgendaItem.Event, TaskyError> {
        return try {
            val result = localDatabaseRepository.getEventById(eventId).toAgendaItem()
//            Log.d("DDD - entity:","${ localDatabaseRepository.getEventById(eventId) }")
//            Log.d("DDD - agendaItem:","agendaItem: $result")
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun updateEvent(
        event: AgendaItem.Event,
        photos: List<ByteArray>
    ): Result<EventResponse, TaskyError> {
        val eventPart = createMultipartEventRequest(event.toEventUpdate())
        val photosPart = photos.mapIndexed { index, photo -> createPhotoPart(photo, index) }

        return try {
            localDatabaseRepository.upsertEvent(event.toEventEntity())
            val result = api.updateEvent(eventPart, photosPart)

            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun deleteEvent(event: AgendaItem.Event): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteEvent(event.toEventEntity())
            api.deleteEventById(event.eventId)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun addReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertReminder(reminder.toReminderEntity())
            api.addReminder(reminder.toSerializedReminder())
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getReminderById(reminderId: String): Result<AgendaItem.Reminder, TaskyError> {
        return try {
            val result = localDatabaseRepository.getReminderById(reminderId).toAgendaItem()
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertReminder(reminder.toReminderEntity())
            api.updateReminder(reminder.toSerializedReminder())
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteReminder(reminder.toReminderEntity())
            api.deleteReminderById(reminder.reminderId)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

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
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun deleteAttendee(eventId: String): Result<Unit, TaskyError> {
        return try {
            api.deleteAttendee(eventId)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }    }

    override suspend fun getLoggedInUserDetails(): Result<AttendeeMinimal, TaskyError> {
        return try {
            val loggedInAttendee = AttendeeMinimal(
                userId = userPrefsRepository.getUserId(),
                fullName = userPrefsRepository.getUserName(),
                email = userPrefsRepository.getUserEmail()
            )
            Result.Success(loggedInAttendee)
        }  catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getAllAgendaItems(selectedDate: LocalDate): Result<Flow<List<AgendaItem>>, TaskyError> {
        return try {
            val localItems = localDatabaseRepository.getAllAgendaItems(selectedDate)
            Result.Success(localItems)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            e.printStackTrace()
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getFullAgenda(): Result<AgendaItems, TaskyError> {
        TODO("Not yet implemented")
    }

    override suspend fun syncAgenda(
        eventIds: List<String>,
        taskIds: List<String>,
        reminderIds: List<String>
    ): Result<Unit, TaskyError> {
        TODO("Not yet implemented")
    }
}