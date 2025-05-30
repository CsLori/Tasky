package com.example.tasky.agenda.agenda_domain.repository

import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface LocalDatabaseRepository {

    fun getAllTasks(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    suspend fun getTaskById(taskId: String): TaskEntity

    suspend fun upsertTask(taskEntity: TaskEntity)

    suspend fun upsertTasks(taskEntities: List<TaskEntity>)

    suspend fun deleteTask(taskId: String)

    fun getAllEvents(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    suspend fun getEventById(eventId: String): EventEntity

    suspend fun upsertEvent(eventEntity: EventEntity)

    suspend fun upsertEvents(eventEntities: List<EventEntity>)

    suspend fun deleteEvent(eventId: String)

    fun getAllReminders(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    suspend fun getReminderById(reminderId: String): ReminderEntity

    suspend fun upsertReminder(reminderEntity: ReminderEntity)

    suspend fun upsertReminders(reminderEntities: List<ReminderEntity>)

    suspend fun deleteReminder(reminderId: String)

    //Agenda items for a specific date
    fun getAllAgendaItemsForDate(selectedDate: LocalDateTime): Flow<List<AgendaItem>>

    fun getAllAgendaItems(): Flow<List<AgendaItem>>

    //Sync agenda functions
    suspend fun insertDeletedAgendaItem(itemForDeletion: AgendaItemForDeletionEntity)

    fun getDeletedItemsByType(type: AgendaOption): Flow<List<AgendaItemForDeletionEntity>>

    suspend fun deleteAllSyncedAgendaItems()

    suspend fun getDeletedItemsForSync(): List<AgendaItemForDeletionEntity>
}