package com.example.tasky.agenda.agenda_data.local.converters

import androidx.room.TypeConverter
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromAttendeeList(attendees: List<Attendee>): String {
        return Json.encodeToString(attendees)
    }

    @TypeConverter
    fun toAttendeeList(data: String): List<Attendee> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromPhotoList(photos: List<Photo>): String {
        return Json.encodeToString(photos)
    }

    @TypeConverter
    fun toPhotoList(data: String): List<Photo> {
        return Json.decodeFromString(data)
    }
}