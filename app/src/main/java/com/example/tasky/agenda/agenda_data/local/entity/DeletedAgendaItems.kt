package com.example.tasky.agenda.agenda_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption

@Entity(tableName = "agenda_items_for_deletion")
data class AgendaItemForDeletion(
    @PrimaryKey val id: String,
    val type: AgendaOption
)