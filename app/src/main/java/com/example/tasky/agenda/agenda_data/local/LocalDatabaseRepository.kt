package com.example.tasky.agenda.agenda_data.local

import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
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

    override suspend fun insertTask(taskEntity: TaskEntity) {
        return taskDao.insertTask(taskEntity)
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        return taskDao.deleteTask(taskEntity)
    }

    override fun getAllEvents(): Flow<List<EventEntity>> {
        return eventDao.getAllEvents()
    }

    override suspend fun insertEvent(eventEntity: EventEntity) {
        return eventDao.insertEvent(eventEntity)
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

    override fun getAllAgendaItems(): Flow<List<Any>> {
        return combine(
            taskDao.getAllTasks(),
            reminderDao.getAllReminders(),
            eventDao.getAllEvents()
        ) { tasks, reminders, events ->
            val combinedList = mutableListOf<Any>()
            combinedList.addAll(tasks)
            combinedList.addAll(reminders)
            combinedList.addAll(events)
            combinedList
        }
    }
}