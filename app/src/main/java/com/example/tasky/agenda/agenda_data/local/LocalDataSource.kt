package com.example.tasky.agenda.agenda_data.local

import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.SyncAgendaItemsDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val reminderDao: ReminderDao,
    private val syncAgendaItemsDao: SyncAgendaItemsDao
) : LocalDatabaseRepository {

    override fun getAllTasks(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks(startOfDay, endOfDay)
    }

    override suspend fun getTaskById(taskId: String): TaskEntity {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun upsertTask(taskEntity: TaskEntity) {
        return taskDao.upsertTask(taskEntity)
    }

    override suspend fun upsertTasks(taskEntities: List<TaskEntity>) {
        return taskDao.upsertTasks(taskEntities)
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        return taskDao.deleteTask(taskEntity)
    }

    override fun getAllEvents(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>> {
        return eventDao.getAllEvents(startOfDay, endOfDay)
    }

    override suspend fun getEventById(eventId: String): EventEntity {
        return eventDao.getEventById(eventId)
    }

    override suspend fun upsertEvent(eventEntity: EventEntity) {
        return eventDao.upsertEvent(eventEntity)
    }

    override suspend fun upsertEvents(eventEntities: List<EventEntity>) {
        return eventDao.upsertEvents(eventEntities)
    }

    override suspend fun deleteEvent(eventEntity: EventEntity) {
        return eventDao.deleteEvent(eventEntity)
    }

    override fun getAllReminders(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>> {
        return reminderDao.getAllReminders(startOfDay, endOfDay)
    }

    override suspend fun getReminderById(reminderId: String): ReminderEntity {
        return reminderDao.getReminderById(reminderId)
    }

    override suspend fun upsertReminder(reminderEntity: ReminderEntity) {
        return reminderDao.upsertReminder(reminderEntity)
    }

    override suspend fun upsertReminders(reminderEntities: List<ReminderEntity>) {
        return reminderDao.upsertReminders(reminderEntities)
    }

    override suspend fun deleteReminder(reminderEntity: ReminderEntity) {
        return reminderDao.deleteReminder(reminderEntity)
    }

    override fun getAllAgendaItems(selectedDate: LocalDateTime): Flow<List<AgendaItem>> {
        val startOfDay = selectedDate.with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = selectedDate.with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return combine(
            taskDao.getAllTasks(startOfDay, endOfDay),
            reminderDao.getAllReminders(startOfDay, endOfDay),
            eventDao.getAllEvents(startOfDay, endOfDay)
        ) { tasks, reminders, events ->
            val combinedList = mutableListOf<AgendaItem>()
            combinedList.addAll(tasks.map { it.toAgendaItem() })
            combinedList.addAll(reminders.map { it.toAgendaItem() })
            combinedList.addAll(events.map { it.toAgendaItem() })
            combinedList.sortedBy { it.time }
        }
    }

    override suspend fun insertDeletedAgendaItem(itemForDeletion: AgendaItemForDeletionEntity) {
        return syncAgendaItemsDao.insertDeletedItem(itemForDeletion)
    }

    override fun getDeletedItemsByType(type: AgendaOption): Flow<List<AgendaItemForDeletionEntity>> {
        return syncAgendaItemsDao.getDeletedItemsByType(type)
    }

    override suspend fun deleteAllSyncedAgendaItems() {
        return syncAgendaItemsDao.clearAllDeletedItems()
    }

    override suspend fun getDeletedItemsForSync(): List<AgendaItemForDeletionEntity> {
        return syncAgendaItemsDao.getAllDeletedItems()
    }
}