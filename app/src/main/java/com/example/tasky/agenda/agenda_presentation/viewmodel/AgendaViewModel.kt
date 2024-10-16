package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.components.AgendaDropdown
import com.example.tasky.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    fun updateState(action: AgendaAction) {
        _state.update {
            when (action) {
                is AgendaAction.UpdateSelectedDate -> it.copy(selectedDate = action.newDate)
                is AgendaAction.UpdateItemSelected -> it.copy(itemSelected = action.item)
                is AgendaAction.UpdateVisibility -> it.copy(isVisible = action.visible)
                is AgendaAction.UpdateIsDateSelectedFromDatePicker -> it.copy(
                    isDateSelectedFromDatePicker = action.isDateSelectedFromDatePicker
                )

                is AgendaAction.UpdateMonth -> it.copy(month = action.month)
                is AgendaAction.UpdateShouldShowDatePicker -> it.copy(shouldShowDatePicker = action.shouldShowDatePicker)
                is AgendaAction.UpdateSelectedIndex -> it.copy(
                    selectedIndex = action.selectedIndex,
                    isDateSelectedFromDatePicker = false
                )
            }
        }
    }

    sealed interface AgendaAction {
        data class UpdateSelectedDate(val newDate: LocalDate) : AgendaAction
        data class UpdateItemSelected(val item: AgendaDropdown?) : AgendaAction
        data class UpdateVisibility(val visible: Boolean) : AgendaAction
        data class UpdateShouldShowDatePicker(val shouldShowDatePicker: Boolean) : AgendaAction
        data class UpdateMonth(val month: String) : AgendaAction
        data class UpdateIsDateSelectedFromDatePicker(val isDateSelectedFromDatePicker: Boolean) :
            AgendaAction

        data class UpdateSelectedIndex(val selectedIndex: Int) :
            AgendaAction
    }

    data class AgendaState(
        val selectedDate: LocalDate = DateUtils.getCurrentDate(),
        val itemSelected: AgendaDropdown? = null,
        val isVisible: Boolean = false,
        val shouldShowDatePicker: Boolean = false,
        val month: String = DateUtils.getCurrentMonth(),
        val isDateSelectedFromDatePicker: Boolean = false,
        val selectedIndex: Int = 0
    )
}