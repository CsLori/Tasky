package com.example.tasky.agenda.agenda_data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromAgendaItemType(value: AgendaItemType): String {
        return value.name
    }

    @TypeConverter
    fun toAgendaItemType(value: String): AgendaItemType {
        return AgendaItemType.valueOf(value)
    }
}