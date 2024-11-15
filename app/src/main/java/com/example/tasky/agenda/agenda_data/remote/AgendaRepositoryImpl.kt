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
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_data.remote.dto.SyncAgendaRequest
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItems
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.util.Logger
import com.example.tasky.util.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.CancellationException

class AgendaRepositoryImpl(
    private val api: TaskyApi,
    private val userPrefsRepository: ProtoUserPrefsRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
) : AgendaRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun addTask(
        task: AgendaItem.Task
    ): Result<Unit, TaskyError> {
        return try {
            localDatabaseRepository.upsertTask(task.toTaskEntity())
            api.addTask(task.toSerializedTask())
            Result.Success(Unit)

        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to create a task: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to fetch a task: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to update a task: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to delete a task: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to create an event: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun getEventById(eventId: String): Result<AgendaItem.Event, TaskyError> {
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

            Logger.e(e, "An error occurred while trying to update an event: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to delete an event: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to create a reminder: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to fetch a reminder: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to update a reminder: %s", e.message)
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

    override suspend fun getAllAgendaItems(selectedDate: LocalDate): Result<Flow<List<AgendaItem>>, TaskyError> {
        return try {
            val localItems = localDatabaseRepository.getAllAgendaItems(selectedDate)

            scope.launch(NonCancellable) {
                val remoteItems = api.getAgenda(selectedDate.toLong())

                if (remoteItems is Result.Success) {
                    upsertAgendaItems(remoteItems.data.toAgendaItems())
                }
            }

            Result.Success(localItems)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to fetch agenda items for agenda screen: %s", e.message)
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

            Logger.e(e, "An error occurred while trying to get full agenda for syncing local db: %s", e.message)
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
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to send sync agenda items: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    //Used for caching the deleted agenda item for when the devices is online again
    override suspend fun insertDeletedAgendaItem(itemForDeletion: AgendaItemForDeletionEntity, networkStatus: NetworkStatus): Result<Unit, TaskyError> {
        return try {
            if (networkStatus == NetworkStatus.Disconnected) {
                localDatabaseRepository.insertDeletedAgendaItem(itemForDeletion)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to insert deleted agenda items: %s", e.message)
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