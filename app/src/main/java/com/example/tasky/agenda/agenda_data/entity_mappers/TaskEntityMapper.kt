package com.example.tasky.agenda.agenda_data.entity_mappers

import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun TaskEntity.toAgendaItem(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = id,
        taskTitle = title,
        taskDescription = description,
        time = time,
        remindAtTime = remindAt,
        isDone = isDone,
        taskReminderType = ReminderType.TASK
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}