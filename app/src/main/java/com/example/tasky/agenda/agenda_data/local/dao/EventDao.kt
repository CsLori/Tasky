package com.example.tasky.agenda.agenda_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE `from` BETWEEN :startOfDay AND :endOfDay")
    fun getAllEvents(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity

    @Query("SELECT EXISTS(SELECT 1 FROM events WHERE id = :eventId)")
    suspend fun existsById(eventId: String): Boolean

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}