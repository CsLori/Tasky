package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import java.time.LocalDate

sealed interface AgendaAction {
    data class UpdateSelectedDate(val newDate: LocalDate) : AgendaAction
    data class UpdateItemSelected(val item: AgendaOption?) : AgendaAction
    data class UpdateVisibility(val visible: Boolean) : AgendaAction
    data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) : AgendaAction
    data class UpdateMonth(val month: String) : AgendaAction
    data class UpdateIsDateSelectedFromDatePicker(val isDateSelectedFromDatePicker: Boolean) :
        AgendaAction
    data class UpdateSelectedIndex(val selectedIndex: Int) :
        AgendaAction
}