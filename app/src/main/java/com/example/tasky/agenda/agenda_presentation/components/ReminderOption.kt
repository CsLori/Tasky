package com.example.tasky.agenda.agenda_presentation.components

import java.util.concurrent.TimeUnit

data class ReminderOption(
    val label: String,
    val timeBeforeInMillis: Long
)

val reminderOptions = listOf(
    ReminderOption("10 minutes before", TimeUnit.MINUTES.toMillis(10)),
    ReminderOption("30 minutes before", TimeUnit.MINUTES.toMillis(30)),
    ReminderOption("1 hour before", TimeUnit.HOURS.toMillis(1)),
    ReminderOption("6 hours before", TimeUnit.HOURS.toMillis(6)),
    ReminderOption("1 day before", TimeUnit.DAYS.toMillis(1))
)