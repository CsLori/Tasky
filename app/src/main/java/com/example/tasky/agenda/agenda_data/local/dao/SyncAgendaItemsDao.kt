package com.example.tasky.agenda.agenda_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletionEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncAgendaItemsDao {
    @Upsert
    suspend fun insertDeletedItem(deletedItem: AgendaItemForDeletionEntity)

    @Query("SELECT * FROM agenda_items_for_deletion WHERE type = :type")
    fun getDeletedItemsByType(type: AgendaOption): Flow<List<AgendaItemForDeletionEntity>>

    @Delete
    suspend fun deleteDeletedItem(deletedItem: AgendaItemForDeletionEntity)

    @Query("DELETE FROM agenda_items_for_deletion")
    suspend fun clearAllDeletedItems()

    @Query("SELECT * FROM agenda_items_for_deletion")
    fun getAllDeletedItems(): Flow<List<AgendaItemForDeletionEntity>>
}