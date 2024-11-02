package com.example.tasky.agenda.agenda_data

import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun createMultipartEventRequest(event: AgendaItem.Event): RequestBody {
    val gson = Gson()
    val jsonEvent = gson.toJson(event)
    return jsonEvent.toRequestBody("application/json".toMediaTypeOrNull())
}

fun createPhotoPart(photo: ByteArray, index: Int): MultipartBody.Part {
    val requestBody = photo.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, photo.size)
    return MultipartBody.Part.createFormData("photo$index", "photo$index.jpg", requestBody)
}