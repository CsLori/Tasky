package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun EventEntity.toAgendaItem(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = id,
        eventTitle = title,
        eventDescription = description,
        from = from,
        to = to,
        remindAtTime = remindAt,
        eventReminderType = ReminderType.EVENT,
        photos = listOf<Photo>(),
        attendees = listOf<Attendee>(),
        isUserEventCreator = false,
        host = null
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
    )
}