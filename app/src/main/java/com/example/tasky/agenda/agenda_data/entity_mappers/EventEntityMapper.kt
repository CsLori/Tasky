package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem

fun EventEntity.toAgendaItem(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = id,
        eventTitle = title,
        eventDescription = description,
        from = from,
        to = to,
        remindAtTime = remindAt,
        photos = photos,
        attendees = attendeeIds,
        isUserEventCreator = isUserEventCreator,
        host = host
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description ?: "",
        from = from,
        to = to,
        remindAt = remindAt,
        attendeeIds = attendees,
        photos = photos,
        isUserEventCreator = isUserEventCreator,
        host = host,
    )
}