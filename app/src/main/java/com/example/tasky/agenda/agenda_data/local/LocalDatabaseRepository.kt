package com.example.tasky.agenda.agenda_data.local

import android.os.Build
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.repository.AgendaItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LocalDatabaseRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val reminderDao: ReminderDao
) : AgendaItemsRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    override suspend fun getTaskById(taskId: String): TaskEntity {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun upsertTask(taskEntity: TaskEntity) {
        return taskDao.upsertTask(taskEntity)
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        return taskDao.deleteTask(taskEntity)
    }

    override fun getAllEvents(): Flow<List<EventEntity>> {
        return eventDao.getAllEvents()
    }

    override suspend fun upsertEvent(eventEntity: EventEntity) {
        return eventDao.upsertEvent(eventEntity)
    }

    override suspend fun deleteEvent(eventEntity: EventEntity) {
        return eventDao.deleteEvent(eventEntity)
    }

    override fun getAllReminders(): Flow<List<ReminderEntity>> {
        return reminderDao.getAllReminders()
    }

    override suspend fun insertReminder(reminderEntity: ReminderEntity) {
        return reminderDao.insertReminder(reminderEntity)
    }

    override suspend fun deleteReminder(reminderEntity: ReminderEntity) {
        return reminderDao.deleteReminder(reminderEntity)
    }

    override fun getAllAgendaItems(): Flow<List<AgendaItem>> {
        return combine(
            taskDao.getAllTasks(),
            reminderDao.getAllReminders(),
            eventDao.getAllEvents()
        ) { tasks, reminders, events ->
            val combinedList = mutableListOf<AgendaItem>()
            combinedList.addAll(tasks.map { it.toAgendaItem() })
            combinedList.addAll(reminders.map { it.toAgendaItem() })
            combinedList.addAll(events.map { it.toAgendaItem() })
            combinedList
        }
    }

    // Will be needed for permission
    fun getPhotoPickerPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}