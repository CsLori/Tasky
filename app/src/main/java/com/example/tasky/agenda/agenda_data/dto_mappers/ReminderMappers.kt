package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.ReminderSerialized
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun AgendaItem.toSerializedReminder(): ReminderSerialized {
    return ReminderSerialized(
        id = id,
        title = title,
        description = description,
        time = time.toLong(),
        remindAt = remindAt.toLong(),
    )
}

fun ReminderSerialized.toReminder(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTime(),
        remindAt = remindAt.toLocalDateTime(),
        details = AgendaItemDetails.Reminder
    )
}