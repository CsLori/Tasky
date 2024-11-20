package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.ReminderEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun ReminderEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTime(),
        remindAt = remindAt.toLocalDateTime(),
        details = AgendaItemDetails.Reminder
    )
}

fun AgendaItem.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        time = time.toLong(),
        remindAt = remindAt.toLong(),
    )
}