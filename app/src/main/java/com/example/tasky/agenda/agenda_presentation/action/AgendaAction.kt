package com.example.tasky.agenda.agenda_presentation.action

import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import java.time.LocalDate

sealed interface AgendaAction {
    data class OnDeleteAgendaItem(val agendaItem: AgendaItem): AgendaAction
    data object OnLogout: AgendaAction
    data object OnFabItemPressed: AgendaAction
    data class OnOpenPressed(val agendaItem: AgendaItem): AgendaAction
    data class OnFilterAgendaItems(val filterDate: Long): AgendaAction
    data object OnIsDoneChange: AgendaAction
    
    // New actions that were previously state updates
    data class OnDateSelected(val newDate: LocalDate) : AgendaAction
    data class OnAgendaOptionSelected(val option: AgendaOption) : AgendaAction
    data class OnMonthChanged(val month: String) : AgendaAction
    data class OnDatePickerSelection(val isDateSelectedFromDatePicker: Boolean) : AgendaAction
    data class OnDayIndexSelected(val selectedIndex: Int, val date: Long) : AgendaAction
    data class OnAgendaItemSelected(val agendaItem: AgendaItem) : AgendaAction
    data class OnTaskCompletionChanged(val agendaItem: AgendaItem, val isDone: Boolean) : AgendaAction
}