package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun TaskEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTime(),
        remindAt = remindAt.toLocalDateTime(),
        details = AgendaItemDetails.Task(isDone = isDone),
    )
}

fun AgendaItem.toTaskEntity(): TaskEntity {
    val taskDetails = details as? AgendaItemDetails.Task
        ?: throw IllegalStateException("Details are not of type Task")
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        time = time.toLong(),
        remindAt = remindAt.toLong(),
        isDone = taskDetails.isDone
    )
}