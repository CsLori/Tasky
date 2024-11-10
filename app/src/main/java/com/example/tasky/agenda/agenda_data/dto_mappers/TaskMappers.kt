package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.TaskSerialized
import com.example.tasky.agenda.agenda_domain.model.AgendaItem

fun AgendaItem.Task.toSerializedTask(): TaskSerialized {
    return TaskSerialized(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone,
    )
}

fun TaskSerialized.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = id,
        taskTitle = title,
        taskDescription = description,
        time = time,
        isDone = isDone,
        remindAtTime = remindAt,
    )
}