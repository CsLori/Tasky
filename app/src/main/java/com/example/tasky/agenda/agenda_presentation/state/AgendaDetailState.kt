@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.state

import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.components.reminderOptions
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class AgendaDetailState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val time: LocalDateTime = LocalDateTime.now(),
    val remindAt: LocalDateTime = reminderOptions[1].timeBeforeInMillis.toLocalDateTime(),
    val details: AgendaItemDetails? = null,
    val isLoading: Boolean = false,
    val isReadOnly: Boolean = false,
    val editType: EditType = EditType.TITLE,
    val selectedDate: LocalDateTime? = null,
    val selectedReminder: Long = reminderOptions[1].timeBeforeInMillis,
    val emailErrorStatus: ErrorStatus? = null,
    val visitorFilter: VisitorFilter = VisitorFilter.ALL,
    val addVisitorEmail: FieldInput? = null,
    val hasDeviceBeenOffline: Boolean = false,
    val isDateSelectedFromDatePicker: Boolean = false,
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

enum class VisitorFilter(val displayName: String) {
    ALL("All"), GOING("Going"), NOT_GOING("Not going")
}

sealed interface AgendaDetailStateUpdate {
    data class UpdateStartTime(val minute: Int, val hour: Int) : AgendaDetailStateUpdate
    data class UpdateEndTime(val minute: Int, val hour: Int) : AgendaDetailStateUpdate
    data class UpdateStartDay(val day: Int, val month: Int, val year: Int) : AgendaDetailStateUpdate
    data class UpdateEndDay(val day: Int, val month: Int, val year: Int) : AgendaDetailStateUpdate
    data class UpdateEditType(val editType: EditType) : AgendaDetailStateUpdate
    data class UpdateSelectedReminder(val selectedReminder: Long) : AgendaDetailStateUpdate
    data class UpdateTitle(val title: String) : AgendaDetailStateUpdate
    data class UpdateDescription(val description: String) : AgendaDetailStateUpdate
    data class UpdateIsReadOnly(val isReadOnly: Boolean) : AgendaDetailStateUpdate
    data class UpdateSelectedAgendaItem(val selectedAgendaItem: AgendaItem) : AgendaDetailStateUpdate
    data class UpdatePhotos(val photos: List<Photo>) : AgendaDetailStateUpdate
    data class UpdateAttendees(val attendees: List<Attendee>) : AgendaDetailStateUpdate
    data class UpdateAddVisitorEmail(val email: FieldInput) : AgendaDetailStateUpdate
    data class UpdateVisitorFilter(val filter: VisitorFilter) : AgendaDetailStateUpdate
    data class UpdateRemindAtTime(val remindAtTime: LocalDateTime) : AgendaDetailStateUpdate
}