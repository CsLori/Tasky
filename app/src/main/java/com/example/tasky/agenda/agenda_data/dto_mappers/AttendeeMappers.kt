package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeDto
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeMinimalDto
import com.example.tasky.agenda.agenda_domain.model.Attendee

fun Attendee.toAttendeeMinimalDto(): AttendeeMinimalDto {
    return AttendeeMinimalDto(
        userId = userId,
        email = email,
        fullName = name,
    )
}

fun List<Attendee>.toAttendeeMinimalDtos(): List<AttendeeMinimalDto> {
    return map { it.toAttendeeMinimalDto() }
}

fun AttendeeMinimalDto.toAttendee(): AttendeeMinimalDto {
    return AttendeeMinimalDto(
        userId = userId,
        email = email,
        fullName = fullName
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        name = name,
        eventId = eventId,
        isGoing = true,
        remindAt = remindAt,
        isCreator = false
    )
}

fun List<AttendeeDto>.toAttendees(): List<Attendee> {
    return map { it.toAttendee() }
}

fun Attendee.toAttendeeDto(): AttendeeDto {
    return AttendeeDto(
        userId = userId,
        email = email,
        name = name,
        eventId = eventId,
        isGoing = true,
        remindAt = remindAt,
    )
}

fun List<Attendee>.toAttendeeDtos(): List<AttendeeDto> {
    return map { it.toAttendeeDto() }
}