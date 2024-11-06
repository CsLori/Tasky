package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.TaskSerialized
import com.example.tasky.agenda.agenda_domain.model.AgendaItem

fun AgendaItem.Task.toSerializedTask(): TaskSerialized {
    return TaskSerialized(
        taskId = id,
        taskTitle = title,
        taskDescription = description,
        time = time,
        remindAtTime = remindAt,
        isDone = isDone,
    )
}

fun TaskSerialized.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = taskId,
        taskTitle = taskTitle,
        taskDescription = taskDescription,
        time = time,
        isDone = isDone,
        remindAtTime = remindAtTime,
    )
}