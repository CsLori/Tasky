package com.example.tasky.agenda.agenda_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.agenda.agenda_domain.model.AgendaOption

@Entity(tableName = "agenda_items_for_deletion")
data class AgendaItemForDeletionEntity(
    @PrimaryKey val id: String,
    val type: AgendaOption
)