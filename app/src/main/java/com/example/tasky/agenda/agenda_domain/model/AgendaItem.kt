package com.example.tasky.agenda.agenda_domain.model

import java.io.Serializable

sealed class AgendaItem(
    @JvmField val id: String,
    @JvmField val title: String,
    @JvmField val description: String?,
    @JvmField val sortDate: Long,
    @JvmField val remindAt: Long,
    @JvmField val reminderType: ReminderType
): Serializable {

    data class Event(
        val eventId: String,
        val eventTitle: String,
        val eventDescription: String?,
        val from: Long,
        val to: Long,
        val photos: List<Photo>,
        val attendees: List<Attendee>,
        val isUserEventCreator: Boolean,
        val host: String?,
        val remindAtTime: Long,
        val eventReminderType: ReminderType
    ) : AgendaItem(eventId, eventTitle, eventDescription, from, remindAtTime, eventReminderType)

    data class Task(
        val taskId: String,
        val taskTitle: String,
        val taskDescription: String?,
        val time: Long,
        val isDone: Boolean,
        val remindAtTime: Long,
        val taskReminderType: ReminderType
    ) : AgendaItem(taskId, taskTitle, taskDescription, time, remindAtTime, taskReminderType)

    data class Reminder(
        val reminderId: String,
        val reminderTitle: String,
        val reminderDescription: String?,
        val time: Long,
        val remindAtTime: Long,
        val reminderReminderType: ReminderType
    ) : AgendaItem(reminderId, reminderTitle, reminderDescription, time, remindAtTime, reminderReminderType)
}