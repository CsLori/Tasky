package com.example.tasky.agenda.agenda_data.local.converters

import androidx.room.TypeConverter
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAttendeeList(attendees: List<Attendee>): String {
        return gson.toJson(attendees)
    }

    @TypeConverter
    fun toAttendeeList(data: String): List<Attendee> {
        val listType = object : TypeToken<List<Attendee>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromPhotoList(photos: List<Photo>): String {
        return gson.toJson(photos)
    }

    @TypeConverter
    fun toPhotoList(data: String): List<Photo> {
        val listType = object : TypeToken<List<Photo>>() {}.type
        return gson.fromJson(data, listType)
    }
}