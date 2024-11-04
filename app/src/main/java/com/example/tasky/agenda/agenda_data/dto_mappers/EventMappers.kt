package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.EventRequest
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun AgendaItem.Event.toEventResponse(): EventResponse {
    return EventResponse(
        id = id,
        title = title,
        description = description ?: "",
        from = from,
        to = to,
        remindAt = remindAt,
        attendees = attendees.toAttendeeDtos(),
        photos = photos.toSerializedPhotos(),
        host = host ?: "",
        isUserEventCreator = isUserEventCreator
    )
}

fun EventResponse.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = id,
        eventTitle = title,
        eventDescription = description,
        from = from,
        to = to,
        remindAtTime = remindAt,
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendees = attendees.toAttendees(),
        photos = photos.toPhotos(),
        eventReminderType = ReminderType.EVENT
    )
}

fun AgendaItem.Event.toEventRequest(): EventRequest {
    return EventRequest(
        id = id,
        title = title,
        description = description ?: "",
        from = from,
        to = to,
        remindAt = remindAt,
        attendeeIds = attendees.map { it.userId },
    )
}