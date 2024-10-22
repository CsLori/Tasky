@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.example.tasky.agenda.agenda_domain.model.Event
import com.example.tasky.agenda.agenda_domain.model.Reminder
import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.agenda.agenda_presentation.components.reminderOptions
import com.example.tasky.util.DateUtils
import com.example.tasky.util.DateUtils.localDateToStringMMMdyyyyFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import java.util.concurrent.TimeUnit

data class AgendaDetailState(
    val task: Task = Task(
        id = UUID.randomUUID().toString(),
        title = "Task",
        description = "Task description",
        time = Instant.now().toEpochMilli(),
        remindAt = Instant.now().toEpochMilli(),
        isDone = false,
    ),
    val event: Event = Event(
        id = UUID.randomUUID().toString(),
        title = "Task",
        description = "Task description",
        from = 1232,
        to = 3434,
        remindAt = Instant.now().toEpochMilli(),
        attendeeIds = emptyList()
    ),
    val reminder: Reminder = Reminder(
        id = UUID.randomUUID().toString(),
        title = "Task",
        description = "Task description",
        time = Instant.now().toEpochMilli(),
        remindAt = Instant.now().toEpochMilli(),
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
    val selectedReminder: Long = reminderOptions[1].timeBeforeInMillis
)

fun Long.toReminderLabel(): String {
    return when (this) {
        TimeUnit.MINUTES.toMillis(10) -> "10 minutes before"
        TimeUnit.MINUTES.toMillis(30) -> "30 minutes before"
        TimeUnit.HOURS.toMillis(1) -> "1 hour before"
        TimeUnit.HOURS.toMillis(6) -> "6 hours before"
        TimeUnit.DAYS.toMillis(1) -> "1 day before"
        else -> {
            "Nothing has been selected"
        }
    }
}

fun defaultReminderSelection() = reminderOptions[1].label


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
}