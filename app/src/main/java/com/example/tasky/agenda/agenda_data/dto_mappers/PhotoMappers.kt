package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.PhotoSerialized
import com.example.tasky.agenda.agenda_domain.model.Photo

fun Photo.toSerializedPhoto(): PhotoSerialized {
    return PhotoSerialized(
        key = key,
        url = url
    )
}

fun List<Photo>.toSerializedPhotos(): List<PhotoSerialized> {
    return map { it.toSerializedPhoto() }

}

fun PhotoSerialized.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}

fun List<PhotoSerialized>.toPhotos(): List<Photo> {
    return map { it.toPhoto() }
}