package com.example.tasky.agenda.agenda_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tasky.agenda.agenda_data.local.converters.Converters
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.SyncAgendaItemsDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, ReminderEntity::class, EventEntity::class, AgendaItemForDeletionEntity::class],
    version = 5,
    exportSchema = false,
)
@TypeConverters(Converters::class)

abstract class AgendaDatabase: RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val reminderDao: ReminderDao
    abstract val eventDao: EventDao
    abstract val syncAgendaItemsDao: SyncAgendaItemsDao

    companion object {
        const val DATABASE_NAME = "agenda_items_db"
    }
}