package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.TaskSerialized
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong

fun AgendaItem.toSerializedTask(): TaskSerialized {
    val taskDetails = details as? AgendaItemDetails.Task
        ?: throw IllegalStateException("Details are not of type Task")
    return TaskSerialized(
        id = id,
        title = title,
        description = description,
        time = time.toLong(),
        remindAt = remindAt.toLong(),
        isDone = taskDetails.isDone,
    )
}

fun TaskSerialized.toTask(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTime(),
        details = AgendaItemDetails.Task(isDone = isDone),
        remindAt = remindAt.toLocalDateTime(),
    )
}