package com.example.tasky.agenda.agenda_data.remote.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.TaskRequest
import com.example.tasky.agenda.agenda_data.remote.dto.TaskResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun AgendaItem.Task.toTaskRequest(): TaskRequest {
    return TaskRequest(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun TaskResponse.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = this.id,
        taskTitle = this.title,
        taskDescription = this.description,
        time = this.time,
        isDone = this.isDone,
        remindAtTime = this.remindAt,
        taskReminderType = ReminderType.TASK
    )
}