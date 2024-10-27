@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.ReminderType
import com.example.tasky.agenda.agenda_presentation.components.reminderOptions
import com.example.tasky.core.presentation.DateUtils
import com.example.tasky.core.presentation.DateUtils.localDateToStringMMMdyyyyFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class AgendaDetailState(
    val task: AgendaItem.Task = AgendaItem.Task(
        taskId = UUID.randomUUID().toString(),
        taskTitle = "Task",
        taskDescription = "Task description",
        time = ZonedDateTime.now().toInstant().toEpochMilli(),
        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
        isDone = false,
        taskReminderType = ReminderType.TASK
    ),
    val event: AgendaItem.Event = AgendaItem.Event(
        eventId = UUID.randomUUID().toString(),
        eventTitle = "Event",
        eventDescription = "Event description",
        from = 1232,
        to = 3434,
        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
        isUserEventCreator = false,
        attendees = emptyList(),
        photos = emptyList(),
        host = null,
        eventReminderType = ReminderType.EVENT
    ),
    val reminder: AgendaItem.Reminder = AgendaItem.Reminder(
        reminderId = UUID.randomUUID().toString(),
        reminderTitle = "Reminder",
        reminderDescription = "Reminder description",
        time = ZonedDateTime.now().toInstant().toEpochMilli(),
        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
        reminderReminderType = ReminderType.REMINDER
    ),
    val isLoading: Boolean = false,
    val shouldShowDatePicker: Boolean = false,
    val date: String = DateUtils.getCurrentDate().localDateToStringMMMdyyyyFormat(),
    val time: TimePickerState = TimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    ),
    val month: String = DateUtils.getCurrentMonth(),
    val isDateSelectedFromDatePicker: Boolean = false,
    val selectedDate: LocalDate = DateUtils.getCurrentDate(),
    val shouldShowTimePicker: Boolean = false,
    val editType: EditType = EditType.TITLE,
    val shouldShowReminderDropdown: Boolean = false,
    val selectedReminder: Long = reminderOptions[1].timeBeforeInMillis,
    val isReadOnly: Boolean = false
)

enum class RemindBeforeDuration(val duration: Duration) {
    TEN_MINUTES(10.minutes),
    THIRTY_MINUTES(30.minutes),
    ONE_HOUR(1.hours),
    SIX_HOURS(6.hours),
    ONE_DAY(1.days)
}

enum class EditType {
    TITLE, DESCRIPTION
}

sealed interface AgendaDetailStateUpdate {
    data class UpdateDate(val newDate: LocalDate) : AgendaDetailStateUpdate
    data class UpdateMonth(val month: String) : AgendaDetailStateUpdate
    data class UpdateTime(val hour: Int, val minute: Int) : AgendaDetailStateUpdate
    data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) :
        AgendaDetailStateUpdate
    data class UpdateShouldShowTimePicker(val shouldShowTimePicker: Boolean) :
        AgendaDetailStateUpdate
    data class UpdateEditType(val editType: EditType) : AgendaDetailStateUpdate
    data class UpdateShouldShowReminderDropdown(val shouldShowReminderDropdown: Boolean) : AgendaDetailStateUpdate
    data class UpdateSelectedReminder(val selectedReminder: Long) : AgendaDetailStateUpdate
    data class UpdateTitle(val title: String) : AgendaDetailStateUpdate
    data class UpdateDescription(val description: String) : AgendaDetailStateUpdate
    data class UpdateIsReadOnly(val isReadOnly: Boolean) : AgendaDetailStateUpdate
}