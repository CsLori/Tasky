package com.example.tasky.agenda.agenda_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val key: String,
    val url: String
)