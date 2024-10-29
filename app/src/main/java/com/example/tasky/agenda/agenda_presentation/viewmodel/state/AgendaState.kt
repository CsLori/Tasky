package com.example.tasky.agenda.agenda_presentation.viewmodel.state

import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.core.presentation.DateUtils
import java.time.LocalDate

data class AgendaState(
    val selectedDate: LocalDate = DateUtils.getCurrentDate(),
    val agendaOption: AgendaOption? = null,
    val isVisible: Boolean = false,
    val shouldShowDatePicker: Boolean = false,
    val month: String = DateUtils.getCurrentMonth(),
    val isDateSelectedFromDatePicker: Boolean = false,
    val selectedIndex: Int = 0,
    val isLoading: Boolean = false,
    val agendaItems: List<AgendaItem> = emptyList(),
    val isAgendaItemReadOnly: Boolean = false,
    val selectedItem: AgendaItem? = null
)