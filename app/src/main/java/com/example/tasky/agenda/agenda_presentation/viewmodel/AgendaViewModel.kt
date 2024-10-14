package com.example.tasky.agenda.agenda_presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasky.agenda.agenda_domain.AgendaRepository
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

    fun setSelectedDate(newDate: LocalDate) {
        _state.update {
            it.copy(
                selectedDate = newDate
            )
        }
    }

    data class AgendaState(
        val selectedDate: LocalDate = LocalDate.now()
    )
}