package com.example.tasky.agenda.agenda_data.remote.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.ReminderRequest
import com.example.tasky.agenda.agenda_data.remote.dto.ReminderResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun AgendaItem.Reminder.toReminderRequest(): ReminderRequest {
    return ReminderRequest(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
    )
}

fun ReminderResponse.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        reminderId = this.id,
        reminderTitle = this.title,
        reminderDescription = this.description,
        time = this.time,
        remindAtTime = this.remindAt,
        reminderReminderType = ReminderType.REMINDER
    )
}