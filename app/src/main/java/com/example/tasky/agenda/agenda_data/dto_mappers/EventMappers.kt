package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.EventRequest
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_data.remote.dto.EventUpdate
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun EventResponse.toEvent(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description,
        time = from.toLocalDateTime(),
        details = AgendaItemDetails.Event(
            toTime = to.toLocalDateTime(),
            attendees = attendees.toAttendees(),
            photos = photos.toPhotos(),
            isUserEventCreator = isUserEventCreator,
            host = host
        ),
        remindAt = remindAt.toLocalDateTime(),
    )
}

fun AgendaItem.toEventRequest(): EventRequest {
    val eventDetails = details as? AgendaItemDetails.Event
        ?: throw IllegalStateException("Details are not of type Event")
    return EventRequest(
        id = id,
        title = title,
        description = description ?: "",
        from = time.toLong(),
        to = eventDetails.toTime.toLong(),
        remindAt = remindAt.toLong(),
        attendeeIds = eventDetails.attendees.map { it.userId },
    )
}

fun AgendaItem.toEventUpdate(deletedPhotoKeys: List<String>): EventUpdate {
    val eventDetails = details as? AgendaItemDetails.Event
        ?: throw IllegalStateException("Details are not of type Event")
    return EventUpdate(
        id = id,
        title = title,
        description = description ?: "",
        from = time.toLong(),
        to = eventDetails.toTime.toLong(),
        remindAt = remindAt.toLong(),
        attendeeIds = eventDetails.attendees.map { it.userId },
        deletedPhotoKeys = deletedPhotoKeys,
        isGoing = true,
    )
}