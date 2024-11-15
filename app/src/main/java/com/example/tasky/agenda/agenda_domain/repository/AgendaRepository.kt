package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.util.NetworkStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AgendaRepository {
    suspend fun addTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun getTaskById(taskId: String): Result<AgendaItem.Task, TaskyError>

    suspend fun updateTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun deleteTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun addEvent(event: AgendaItem.Event, photos: List<ByteArray>): Result<EventResponse, TaskyError>

    suspend fun getEventById(eventId: String): Result<AgendaItem.Event, TaskyError>

    suspend fun updateEvent(event: AgendaItem.Event, photos: List<ByteArray>): Result<EventResponse, TaskyError>

    suspend fun deleteEvent(event: AgendaItem.Event): Result<Unit, TaskyError>

    suspend fun addReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun getReminderById(reminderId: String): Result<AgendaItem.Reminder, TaskyError>

    suspend fun updateReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun deleteReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun getAttendee(email: String): Result<AttendeeExistDto, TaskyError>

    suspend fun deleteAttendee(eventId: String): Result<Unit, TaskyError>

    suspend fun getLoggedInUserDetails(): Result<AttendeeMinimal, TaskyError>

    //Used for querying all the items for a selected day
    suspend fun getAllAgendaItems(selectedDate: LocalDate): Result<Flow<List<AgendaItem>>, TaskyError>

    //Local sync for when user logs in
    suspend fun getFullAgenda(): Result<Unit,TaskyError>

    suspend fun syncAgenda(): Result<Unit, TaskyError>

    suspend fun insertDeletedAgendaItem(itemForDeletion: AgendaItemForDeletionEntity, networkStatus: NetworkStatus): Result<Unit, TaskyError>
}