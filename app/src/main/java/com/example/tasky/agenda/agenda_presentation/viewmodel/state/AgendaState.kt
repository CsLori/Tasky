package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.util.DateUtils
import java.time.LocalDate

data class AgendaState(
    val selectedDate: LocalDate = DateUtils.getCurrentDate(),
    val itemSelected: AgendaOption? = null,
    val isVisible: Boolean = false,
    val shouldShowDatePicker: Boolean = false,
    val month: String = DateUtils.getCurrentMonth(),
    val isDateSelectedFromDatePicker: Boolean = false,
    val selectedIndex: Int = 0,
    val isLoading: Boolean = false,
    val agendaItems: List<Any> = emptyList()
)