package com.example.tasky.agenda.agenda_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, ReminderEntity::class, EventEntity::class],
    version = 3,
    exportSchema = false,
)
//@TypeConverters(Converters::class)

abstract class AgendaDatabase: RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val reminderDao: ReminderDao
    abstract val eventDao: EventDao

    companion object {
        const val DATABASE_NAME = "agenda_items_db"
    }
}