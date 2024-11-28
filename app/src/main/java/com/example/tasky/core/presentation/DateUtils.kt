package com.example.tasky.core.presentation

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    /**
     * Returns a list of pairs, where each pair contains:
     * - The first letter of the day of the week (String)
     * - The day of the month (Int)
     *
     * @param numberOfDays the number of days to generate, starting from today
     */
    fun getDaysWithDates(dateTime: LocalDate?, numberOfDays: Int): List<Pair<String?, Int?>> {
        val today: LocalDate = dateTime ?: LocalDate.now()
        // Create a list of dates from today to the next numberOfDays
        val days = (0..numberOfDays).map { today.plusDays(it.toLong()) }

        // Transform the list of LocalDate into pairs (first letter of day, day of month)
        return days.map { date ->
            val dayLetter =
                date?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.getDefault())?.get(0)
                    ?.toString()
            val dayNumber = date?.dayOfMonth
            dayLetter to dayNumber
        }
    }

    fun longToLocalDate(date: Long): LocalDate {
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }


    fun convertMillisToLocalDate(millis: Long): ZonedDateTime {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
    }

    fun getCurrentMonth(): String = LocalDateTime.now().month.toString()

    fun getCurrentDate(): LocalDate = LocalDate.now()

    fun LocalDate.localDateToStringddMMMMyyyyFormat(): String {
        return this.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }

    // Output Jul 15, 2024
    fun Long.toLocalizedDateFormat(): String {
        val localDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

        return localDateTime.format(formatter)
    }

    // Output Mar 15, 10:00
    fun LocalDateTime.toMMMdHHmmFormat(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm")
        return this.format(formatter)
    }

    fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun LocalDate.toLong(timeZone: ZoneId = ZoneId.systemDefault()): Long {
        return this.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun Long.toLocalDateTime(): LocalDateTime {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    fun LocalDateTime.toLong(): Long {
        return this.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    // Output 15:00
    fun LocalDateTime.toHourMinuteFormat(): String {
        val hours = this.hour
        val minutes = this.minute

        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
    }

    fun LocalDateTime.toStringMMMdyyyyFormat(): String {
        return this.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
    }
}