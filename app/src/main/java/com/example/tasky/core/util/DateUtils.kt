package com.example.tasky.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun longToLocalDate(date: Long) =
        LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault())

    fun convertMillisToLocalDate(millis: Long): ZonedDateTime {
        // Interpret the milliseconds as the start of the day in UTC, then convert to Los Angeles time
        val utcDateAtStartOfDay = Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        // Convert to the same instant in Local time zone
        val localDate = utcDateAtStartOfDay.atStartOfDay(ZoneId.systemDefault())

        return localDate

    }

    fun getCurrentMonth(): String = LocalDate.now().month.toString()

    fun LocalDate.localDateToStringddMMMMyyyyFormat(): String {
        return this.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }
}