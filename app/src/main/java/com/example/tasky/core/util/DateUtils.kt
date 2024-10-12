package com.example.tasky.core.util

import java.time.LocalDate
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
    fun getDaysWithDates(numberOfDays: Int): List<Pair<String, Int>> {
        val today = LocalDate.now()
        // Create a list of dates from today to the next numberOfDays
        val days = (0..numberOfDays).map { today.plusDays(it.toLong()) }

        // Transform the list of LocalDate into pairs (first letter of day, day of month)
        return days.map { date ->
            val dayLetter =
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())[0].toString()
            val dayNumber = date.dayOfMonth
            dayLetter to dayNumber
        }
    }

    fun getCurrentMonth(): String = LocalDate.now().month.toString()
}