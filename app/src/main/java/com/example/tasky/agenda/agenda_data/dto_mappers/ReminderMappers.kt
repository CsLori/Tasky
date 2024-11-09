package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.ReminderSerialized
import com.example.tasky.agenda.agenda_domain.model.AgendaItem

fun AgendaItem.Reminder.toSerializedReminder(): ReminderSerialized {
    return ReminderSerialized(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
    )
}

fun ReminderSerialized.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        reminderId = id,
        reminderTitle = title,
        reminderDescription = description,
        time = time,
        remindAtTime = remindAt,
    )
}