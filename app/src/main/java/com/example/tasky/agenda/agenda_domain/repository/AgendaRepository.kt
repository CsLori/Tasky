package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError

interface AgendaRepository {
    suspend fun addTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun updateTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun deleteTask(task: AgendaItem.Task): Result<Unit, TaskyError>

    suspend fun addEvent(event: AgendaItem.Event, photos: List<ByteArray>): Result<EventResponse, TaskyError>

    suspend fun updateEvent(event: AgendaItem.Event, photos: List<ByteArray>): Result<EventResponse, TaskyError>

    suspend fun deleteEvent(event: AgendaItem.Event): Result<Unit, TaskyError>

    suspend fun addReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun updateReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun deleteReminder(reminder: AgendaItem.Reminder): Result<Unit, TaskyError>

    suspend fun getAttendee(email: String): Result<AttendeeExistDto, TaskyError>

}