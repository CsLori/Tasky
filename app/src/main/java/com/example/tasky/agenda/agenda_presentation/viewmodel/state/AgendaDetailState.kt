package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.components.reminderOptions
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class AgendaDetailState(
    val title: String = "",
    val description: String = "",
    val time: LocalDateTime = LocalDateTime.now(),
    val remindAt: LocalDateTime? = null,
    val details: AgendaItemDetails? = null,
    val isLoading: Boolean = false,
    val isReadOnly: Boolean = false,
    val secondRowDate: LocalDateTime? = null,
    val shouldShowDatePicker: Boolean = false,
    val shouldShowSecondRowDatePicker: Boolean = false,
    val shouldShowTimePicker: Boolean = false,
    val shouldShowSecondRowTimePicker: Boolean = false,
    val shouldShowReminderDropdown: Boolean = false,
    val editType: EditType = EditType.TITLE,
    val selectedAgendaItem: AgendaItemDetails? = null,
    val selectedDate: LocalDateTime? = null,
    val selectedReminder: Long = reminderOptions[1].timeBeforeInMillis,
    val emailErrorStatus: ErrorStatus? = null,
    val visitorFilter: VisitorFilter = VisitorFilter.ALL,
    val addVisitorEmail: FieldInput? = null,
    val hasDeviceBeenOffline: Boolean = false,
    val isDateSelectedFromDatePicker: Boolean = false,
)

sealed interface AgendaItemDetails {
    data class Event(
        val toTime: LocalDateTime,
        val attendees: List<Attendee>,
        val photos: List<Photo>,
        val isUserEventCreator: Boolean,
        val host: String?
    ): AgendaItemDetails

    data class Task(
        val isDone: Boolean
    ): AgendaItemDetails

    data object Reminder: AgendaItemDetails
}

//    val task: AgendaItem.Task = AgendaItem.Task(
//        taskId = UUID.randomUUID().toString(),
//        taskTitle = "Task",
//        taskDescription = "Task description",
//        time = ZonedDateTime.now().toInstant().toEpochMilli(),
//        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
//        isDone = false,
//    ),
//    val event: AgendaItem.Event = AgendaItem.Event(
//        eventId = UUID.randomUUID().toString(),
//        eventTitle = "Event",
//        eventDescription = "Event description",
//        from = ZonedDateTime.now().toInstant().toEpochMilli(),
//        to = ZonedDateTime.now().toInstant().toEpochMilli(),
//        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
//        isUserEventCreator = false,
//        attendees = emptyList(),
//        photos = emptyList(),
//        host = null,
//    ),
//    val reminder: AgendaItem.Reminder = AgendaItem.Reminder(
//        reminderId = UUID.randomUUID().toString(),
//        reminderTitle = "Reminder",
//        reminderDescription = "Reminder description",
//        time = ZonedDateTime.now().toInstant().toEpochMilli(),
//        remindAtTime = ZonedDateTime.now().toInstant().toEpochMilli(),
//    ),
//    val isLoading: Boolean = false,
//    val shouldShowDatePicker: Boolean = false,
//    val shouldShowSecondRowDatePicker: Boolean = false,
//    val date: LocalDate = LocalDate.now(),
//    val secondRowDate: String = DateUtils.getCurrentDate().localDateToStringMMMdyyyyFormat(),
//    val fromAtTime: TimePickerState = TimePickerState(
//        initialHour = LocalTime.now().hour,
//        initialMinute = LocalTime.now().minute,
//        is24Hour = true
//        is24Hour = true
//    ),
//    val toTime: TimePickerState = TimePickerState(
//        initialHour = LocalTime.now().hour,
//        initialMinute = LocalTime.now().minute,
//        is24Hour = true
//    ),
//    val isDateSelectedFromDatePicker: Boolean = false,
//    val selectedDate: LocalDate = DateUtils.getCurrentDate(),
//    val shouldShowTimePicker: Boolean = false,
//    val shouldShowSecondRowTimePicker: Boolean = false,
//    val editType: EditType = EditType.TITLE,
//    val shouldShowReminderDropdown: Boolean = false,
//    val selectedReminder: Long = reminderOptions[1].timeBeforeInMillis,
//    val isReadOnly: Boolean = false,
//    val selectedAgendaItem: AgendaItem? = null,
//    val addVisitorEmail: FieldInput? = null,
//    val emailErrorStatus: ErrorStatus? = null,
//    val visitorFilter: VisitorFilter = VisitorFilter.ALL,
//    val hasDeviceBeenOffline: Boolean = false
//)

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

enum class VisitorFilter(val displayName: String) {
    ALL("All"), GOING("Going"), NOT_GOING("Not going")
}

sealed interface AgendaDetailStateUpdate {
    data class UpdateTime(val date: LocalDateTime) : AgendaDetailStateUpdate
    data class UpdateEventSecondRowDate(val date: LocalDateTime) : AgendaDetailStateUpdate
//    data class UpdateFromAtTime(val hour: Int, val minute: Int) : AgendaDetailStateUpdate
    data class UpdateFromAtTime(val time: LocalDateTime) : AgendaDetailStateUpdate // this is probably not needed, UpdateTime will do it
//    data class UpdateEventSecondRowTime(val hour: Int, val minute: Int) : AgendaDetailStateUpdate
    data class UpdateEventSecondRowTime(val toTime: LocalDateTime) : AgendaDetailStateUpdate
    data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) : AgendaDetailStateUpdate
    data class UpdateShouldShowSecondRowDatePicker(val shouldShowSecondRowDatePicker: Boolean) : AgendaDetailStateUpdate
    data class UpdateShouldShowTimePicker(val shouldShowTimePicker: Boolean) : AgendaDetailStateUpdate
    data class UpdateShouldShowSecondRowTimePicker(val shouldShowTimePicker: Boolean) : AgendaDetailStateUpdate
    data class UpdateEditType(val editType: EditType) : AgendaDetailStateUpdate
    data class UpdateShouldShowReminderDropdown(val shouldShowReminderDropdown: Boolean) : AgendaDetailStateUpdate
    data class UpdateSelectedReminder(val selectedReminder: Long) : AgendaDetailStateUpdate
    data class UpdateTitle(val title: String) : AgendaDetailStateUpdate
    data class UpdateDescription(val description: String) : AgendaDetailStateUpdate
    data class UpdateIsReadOnly(val isReadOnly: Boolean) : AgendaDetailStateUpdate
    data class UpdateSelectedAgendaItem(val selectedAgendaItem: AgendaItemDetails?) : AgendaDetailStateUpdate
    data class UpdatePhotos(val photos: List<Photo>) : AgendaDetailStateUpdate
    data class UpdateAttendees(val attendees: List<Attendee>) : AgendaDetailStateUpdate
    data class UpdateAddVisitorEmail(val email: FieldInput) : AgendaDetailStateUpdate
    data class UpdateVisitorFilter(val filter: VisitorFilter) : AgendaDetailStateUpdate
    data class UpdateRemindAtTime(val remindAtTime: LocalDateTime?) : AgendaDetailStateUpdate
//    data class UpdateSortDate(val sortDate: Long) : AgendaDetailStateUpdate
    data class UpdateSecondRowToDate(val toDate: LocalDateTime) : AgendaDetailStateUpdate
}