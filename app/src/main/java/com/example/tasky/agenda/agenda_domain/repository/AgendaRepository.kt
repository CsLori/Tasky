package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface AgendaRepository {
    suspend fun addTask(task: AgendaItem): Result<Unit, TaskyError>

    suspend fun getTaskById(taskId: String): Result<AgendaItem, TaskyError>

    suspend fun updateTask(task: AgendaItem, shouldScheduleAlarm: Boolean = true): Result<Unit, TaskyError>

    suspend fun deleteTask(taskId: String): Result<Unit, TaskyError>

    suspend fun addEvent(event: AgendaItem, photos: List<ByteArray>): Result<EventResponse, TaskyError>

    suspend fun getEventById(eventId: String): Result<AgendaItem, TaskyError>

    suspend fun updateEvent(event: AgendaItem, photos: List<ByteArray>, photosToDelete: List<String>): Result<EventResponse, TaskyError>

    suspend fun deleteEvent(eventId: String): Result<Unit, TaskyError>

    suspend fun addReminder(reminder: AgendaItem): Result<Unit, TaskyError>

    suspend fun getReminderById(reminderId: String): Result<AgendaItem, TaskyError>

    suspend fun updateReminder(reminder: AgendaItem): Result<Unit, TaskyError>

    suspend fun deleteReminder(reminderId: String): Result<Unit, TaskyError>

    suspend fun getAttendee(email: String): Result<AttendeeExistDto, TaskyError>

    suspend fun deleteAttendee(eventId: String): Result<Unit, TaskyError>

    suspend fun getLoggedInUserDetails(): Result<AttendeeMinimal, TaskyError>

    //Used for querying all the items for a selected day
    suspend fun getAllAgendaItemsForDate(selectedDate: LocalDateTime): Result<Flow<List<AgendaItem>>, TaskyError>

    //Local sync for when user logs in
    suspend fun getFullAgenda(): Result<Unit,TaskyError>

    suspend fun syncAgenda(): Result<Unit, TaskyError>
}