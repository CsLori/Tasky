package com.example.tasky.agenda.agenda_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE remindAt BETWEEN :startOfDay AND :endOfDay")
    fun getAllReminders(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): ReminderEntity

    @Query("SELECT EXISTS(SELECT 1 FROM reminders WHERE id = :reminderId)")
    suspend fun existsById(reminderId: String): Boolean

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}