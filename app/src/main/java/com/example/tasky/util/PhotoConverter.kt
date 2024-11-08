package com.example.tasky.util

import android.content.Context
import android.net.Uri
import com.example.tasky.agenda.agenda_domain.model.Photo
import kotlinx.coroutines.CancellationException

class PhotoConverter(
    private val context: Context
) {
    suspend fun convertPhotoToByteArray(photo: Photo): ByteArray? {
        return try {
            val uri = Uri.parse(photo.url)
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.readBytes()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    suspend fun convertPhotosToByteArrays(photos: List<Photo>): List<ByteArray> {
        return photos.mapNotNull { photo ->
            convertPhotoToByteArray(photo)
        }
    }
}