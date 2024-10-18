@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.util.DateUtils
import com.example.tasky.util.DateUtils.localDateToStringMMMdyyyyFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AgendaDetailState(
    val task: Task = Task(
        id = UUID.randomUUID().toString(),
        title = "Task",
        description = "Task description",
        time = Instant.now().toEpochMilli(),
        remindAt = Instant.now().toEpochMilli(),
        isDone = false,
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
)

sealed interface AgendaDetailStateUpdate {
    data class UpdateDate(val newDate: LocalDate) : AgendaDetailStateUpdate
    data class UpdateMonth(val month: String) : AgendaDetailStateUpdate
    data class UpdateTime(val hour: Int, val minute: Int) : AgendaDetailStateUpdate
    data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) :
        AgendaDetailStateUpdate
    data class UpdateShouldShowTimePicker(val shouldShowTimePicker: Boolean) :
        AgendaDetailStateUpdate
}