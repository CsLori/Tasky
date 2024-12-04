package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.agenda.agenda_data.createMultipartEventRequest
import com.example.tasky.agenda.agenda_data.createPhotoPart
import com.example.tasky.agenda.agenda_data.dto_mappers.toAgendaItems
import com.example.tasky.agenda.agenda_data.dto_mappers.toEventRequest
import com.example.tasky.agenda.agenda_data.dto_mappers.toEventUpdate
import com.example.tasky.agenda.agenda_data.dto_mappers.toSerializedReminder
import com.example.tasky.agenda.agenda_data.dto_mappers.toSerializedTask
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.entity_mappers.toEventEntity
import com.example.tasky.agenda.agenda_data.entity_mappers.toReminderEntity
import com.example.tasky.agenda.agenda_data.entity_mappers.toTaskEntity
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_data.remote.dto.SyncAgendaRequest
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItems
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import com.example.tasky.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(
    private val api: TaskyApi,
    private val userPrefsRepository: UserPrefsRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
    private val  alarmScheduler: AlarmScheduler
) : AgendaRepository {

    override suspend fun addTask(
        task: AgendaItem
    ): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertTask(task.toTaskEntity())
            alarmScheduler.schedule(task, AgendaOption.TASK)
            api.addTask(task.toSerializedTask())
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to create a task: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getTaskById(taskId: String): Result<AgendaItem, TaskyError> {
        return try {
            val result = localDatabaseRepository.getTaskById(taskId).toAgendaItem()
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to fetch a task: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }


    override suspend fun updateTask(task: AgendaItem, shouldScheduleAlarm: Boolean): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertTask(task.toTaskEntity())
            if(shouldScheduleAlarm) {
                alarmScheduler.schedule(task, AgendaOption.TASK)
            }
            api.updateTask(task.toSerializedTask())
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to update a task: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }


    override suspend fun deleteTask(taskId: String): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteTask(taskId)
            val remoteResult = api.deleteTaskById(taskId)

            // If they api call fails, we would like to save the deleted task, event, reminder
            if (!remoteResult.isSuccessful) {
                insertDeletedAgendaItem(AgendaItemForDeletionEntity(taskId, AgendaOption.TASK))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to delete a task: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun addEvent(
        event: AgendaItem,
        photos: List<ByteArray>
    ): Result<EventResponse, TaskyError> {
        val eventPart = createMultipartEventRequest(event.toEventRequest())
        val photosPart = photos.mapIndexed { index, photo -> createPhotoPart(photo, index) }

        return try {
            localDatabaseRepository.upsertEvent(event.toEventEntity())
            alarmScheduler.schedule(event, AgendaOption.EVENT)
            val result = api.addEvent(eventPart, photosPart)

            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to create an event: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getEventById(eventId: String): Result<AgendaItem, TaskyError> {
        return try {
            val result = localDatabaseRepository.getEventById(eventId).toAgendaItem()
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to fetch an event: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun updateEvent(
        event: AgendaItem,
        photos: List<ByteArray>,
        photosToDelete: List<String>
    ): Result<EventResponse, TaskyError> {
        val eventPart = createMultipartEventRequest(event.toEventUpdate(photosToDelete))
        val photosPart = photos.mapIndexed { index, photo -> createPhotoPart(photo, index) }

        return try {
            localDatabaseRepository.upsertEvent(event.toEventEntity())
            alarmScheduler.schedule(event, AgendaOption.EVENT)
            val result = api.updateEvent(eventPart, photosPart)

            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to update an event: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteEvent(eventId)
            val remoteResult = api.deleteEventById(eventId)

            if (!remoteResult.isSuccessful) {
                insertDeletedAgendaItem(AgendaItemForDeletionEntity(eventId, AgendaOption.EVENT))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to delete an event: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun addReminder(reminder: AgendaItem): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertReminder(reminder.toReminderEntity())
            alarmScheduler.schedule(reminder, AgendaOption.REMINDER)
            api.addReminder(reminder.toSerializedReminder())
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to create a reminder: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getReminderById(reminderId: String): Result<AgendaItem, TaskyError> {
        return try {
            val result = localDatabaseRepository.getReminderById(reminderId).toAgendaItem()
            Result.Success(result)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to fetch a reminder: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertReminder(reminder.toReminderEntity())
            alarmScheduler.schedule(reminder, AgendaOption.REMINDER)
            api.updateReminder(reminder.toSerializedReminder())
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to update a reminder: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.deleteReminder(reminderId)
            val remoteResult = api.deleteReminderById(reminderId)

            if (!remoteResult.isSuccessful) {
                insertDeletedAgendaItem(AgendaItemForDeletionEntity(reminderId, AgendaOption.REMINDER))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to delete a reminder: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to fetch attendee: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to delete attendee: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getLoggedInUserDetails(): Result<AttendeeMinimal, TaskyError> {
        return try {
            val loggedInAttendee = AttendeeMinimal(
                userId = userPrefsRepository.getUserId(),
                fullName = userPrefsRepository.getUserName(),
                email = userPrefsRepository.getUserEmail()
            )
            Result.Success(loggedInAttendee)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to fetch user details: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getAllAgendaItemsForDate(selectedDate: LocalDateTime): Result<Flow<List<AgendaItem>>, TaskyError> {
        return try {
            val localItems = localDatabaseRepository.getAllAgendaItemsForDate(selectedDate)

            //This request requires UTC
            val timeStamp = selectedDate.toInstant(ZoneOffset.UTC).toEpochMilli()
            val remoteItems = api.getAgenda(timeStamp)

            withContext(Dispatchers.IO) {
                upsertAgendaItems(remoteItems.toAgendaItems())
            }

            Result.Success(localItems)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(
                e,
                "An error occurred while trying to fetch agenda items for agenda screen: %s",
                e.message
            )
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    //This should be called when the user logs in and we want to get
    //the full agenda for local cache
    override suspend fun getFullAgenda(): Result<Unit, TaskyError> {
        return try {
            val remoteItems = api.getFullAgenda()
            upsertAgendaItems(remoteItems.toAgendaItems())

            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(
                e,
                "An error occurred while trying to get full agenda for syncing local db: %s",
                e.message
            )
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    //This should be called when the device is online again
    override suspend fun syncAgenda(): Result<Unit, TaskyError> {
        return try {
            val eventIds = localDatabaseRepository.getDeletedItemsByType(AgendaOption.EVENT)
                .first()
                .map { it.id }
            val taskIds = localDatabaseRepository.getDeletedItemsByType(AgendaOption.TASK)
                .first()
                .map { it.id }
            val reminderIds = localDatabaseRepository.getDeletedItemsByType(AgendaOption.REMINDER)
                .first()
                .map { it.id }

            val result = api.syncAgenda(SyncAgendaRequest(eventIds, taskIds, reminderIds))

            if (result.isSuccessful) {
                localDatabaseRepository.deleteAllSyncedAgendaItems()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to send sync agenda items: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    //Used for caching the deleted agenda item for when the devices is online again
    private suspend fun insertDeletedAgendaItem(
        itemForDeletion: AgendaItemForDeletionEntity,
    ): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.insertDeletedAgendaItem(itemForDeletion)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(
                e,
                "An error occurred while trying to insert deleted agenda items: %s",
                e.message
            )
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    private suspend fun upsertAgendaItems(localItems: AgendaItems) {
        val eventEntities = localItems.events.map { it.toEventEntity() }
        val taskEntities = localItems.tasks.map { it.toTaskEntity() }
        val reminderEntities = localItems.reminders.map { it.toReminderEntity() }

        localDatabaseRepository.upsertEvents(eventEntities)
        localDatabaseRepository.upsertTasks(taskEntities)
        localDatabaseRepository.upsertReminders(reminderEntities)
    }
}