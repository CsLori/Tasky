package com.example.tasky.agenda.agenda_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotoSerialized(
    val key: String,
    val url: String
)