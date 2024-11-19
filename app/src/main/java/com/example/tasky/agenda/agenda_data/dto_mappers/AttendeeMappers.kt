package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeDto
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeMinimalDto
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.AttendeeMinimal

fun AttendeeMinimal.toAttendeeMinimalDto(): AttendeeMinimalDto {
    return AttendeeMinimalDto(
        userId = userId,
        email = email,
        fullName = fullName,
    )
}

fun List<AttendeeMinimal>.toAttendeeMinimalDtos(): List<AttendeeMinimalDto> {
    return map { it.toAttendeeMinimalDto() }
}

fun AttendeeMinimalDto.toAttendeeMinimal(): AttendeeMinimal {
    return AttendeeMinimal(
        userId = userId,
        email = email,
        fullName = fullName
    )
}

fun AttendeeDto.toAttendee(isCreator: Boolean = false): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        name = fullName,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
        isCreator = isCreator
    )
}

fun List<AttendeeDto>.toAttendees(): List<Attendee> {
    return map { it.toAttendee() }
}

fun Attendee.toAttendeeDto(): AttendeeDto {
    return AttendeeDto(
        userId = userId,
        email = email,
        fullName = name,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
    )
}

fun List<Attendee>.toAttendeeDtos(): List<AttendeeDto> {
    return map { it.toAttendeeDto() }
}

fun AttendeeMinimalDto.toAttendee(eventId: String, remindAt: Long, isGoing: Boolean = true, isCreator: Boolean = false): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        name = fullName,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
        isCreator = isCreator
    )
}