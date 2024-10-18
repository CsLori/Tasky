package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_data.remote.AgendaRepositoryImpl
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepositoryImpl
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
}