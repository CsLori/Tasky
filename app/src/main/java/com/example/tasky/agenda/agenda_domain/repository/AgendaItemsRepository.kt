package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import kotlinx.coroutines.flow.Flow

interface AgendaItemsRepository {

    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun insertTask(taskEntity: TaskEntity)

    suspend fun deleteTask(taskEntity: TaskEntity)

    fun getAllEvents(): Flow<List<EventEntity>>

    suspend fun insertEvent(eventEntity: EventEntity)

    suspend fun deleteEvent(eventEntity: EventEntity)

    fun getAllReminders(): Flow<List<ReminderEntity>>

    suspend fun insertReminder(reminderEntity: ReminderEntity)

    suspend fun deleteReminder(reminderEntity: ReminderEntity)

    fun getAllAgendaItems(): Flow<List<AgendaItem>>
}