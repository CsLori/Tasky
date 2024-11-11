package com.example.tasky.agenda.agenda_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.agenda.agenda_data.local.entity.AgendaItemForDeletion
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption

@Dao
interface DeletedAgendaItemsDao {
    @Upsert
    suspend fun insertDeletedItem(deletedItem: AgendaItemForDeletion)

    @Query("SELECT * FROM agenda_items_for_deletion WHERE type = :type")
    suspend fun getDeletedItemsByType(type: AgendaOption): List<AgendaItemForDeletion>

    @Delete
    suspend fun deleteDeletedItem(deletedItem: AgendaItemForDeletion)

    @Query("DELETE FROM agenda_items_for_deletion")
    suspend fun clearAllDeletedItems()

}