package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeSerialized
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun Attendee.toSerializedAttendee(): AttendeeSerialized {
    return AttendeeSerialized(
        userId = userId,
        email = email,
        name = name,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toLong(),
        isCreator = isCreator
    )
}

fun List<Attendee>.toSerializedAttendees(): List<AttendeeSerialized> {
    return map { it.toSerializedAttendee() }
}

fun AttendeeSerialized.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        name = name,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toLocalDateTime(),
        isCreator = isCreator
    )
}

fun List<AttendeeSerialized>.toAttendees(): List<Attendee> {
    return map { it.toAttendee() }
}