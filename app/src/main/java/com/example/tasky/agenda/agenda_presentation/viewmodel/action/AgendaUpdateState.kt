package com.example.tasky.agenda.agenda_presentation.viewmodel.action

import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import java.time.LocalDate

sealed interface AgendaUpdateState {
    data class UpdateSelectedDate(val newDate: LocalDate) : AgendaUpdateState
    data class UpdateItemSelected(val item: AgendaOption?) : AgendaUpdateState
    data class UpdateVisibility(val visible: Boolean) : AgendaUpdateState
    data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) : AgendaUpdateState
    data class UpdateMonth(val month: String) : AgendaUpdateState
    data class UpdateIsDateSelectedFromDatePicker(val isDateSelectedFromDatePicker: Boolean) :
        AgendaUpdateState
    data class UpdateSelectedIndex(val selectedIndex: Int) :
        AgendaUpdateState
    data class UpdateIsAgendaItemReadOnly(val isAgendaItemReadOnly: Boolean) : AgendaUpdateState
}