package com.example.tasky.agenda.agenda_domain.model

sealed class AgendaItem(
    val id: String,
    val title: String,
    val description: String?,
    val sortDate: Long,
    val remindAt: Long,
    val reminderType: ReminderType
) {

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