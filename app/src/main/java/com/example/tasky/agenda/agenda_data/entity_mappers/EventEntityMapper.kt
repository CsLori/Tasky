package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.EventEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun EventEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description ?: "",
        time = from.toLocalDateTime(),
        details = AgendaItemDetails.Event(
            toTime = to.toLocalDateTime(),
            attendees = attendeeIds,
            photos = photos,
            isUserEventCreator = isUserEventCreator,
            host = host
        ),
        remindAt = remindAt.toLocalDateTime()
    )
}

fun AgendaItem.toEventEntity(): EventEntity {
    val eventDetails = details as? AgendaItemDetails.Event
        ?: throw IllegalStateException("Details are not of type Event")
    return EventEntity(
        id = id,
        title = title,
        description = description ?: "",
        from = time.toLong(),
        to = eventDetails.toTime.toLong(),
        remindAt = remindAt.toLong(),
        attendeeIds = eventDetails.attendees,
        photos = eventDetails.photos,
        isUserEventCreator = eventDetails.isUserEventCreator,
        host = eventDetails.host,
    )
}