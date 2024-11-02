package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.EventRequest
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem

fun AgendaItem.Event.toEventRequest(): EventRequest {
    return EventRequest(
        id = id,
        title = title,
        description = description ?: "",
        from = from,
        to = to,
        remindAt = remindAt,
        attendeeIds = attendees.map { it.userId }
    )
}

fun EventResponse.toEvent(): EventResponse {
    return EventResponse(
        id = id,
        title = title,
        description = description,
        from = from,
        to = to,
        remindAt = remindAt,
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendees = attendees,
        photos = photos
    )
}