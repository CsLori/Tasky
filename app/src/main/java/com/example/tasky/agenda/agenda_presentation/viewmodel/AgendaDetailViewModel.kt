@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_data.dto_mappers.toAttendee
import com.example.tasky.agenda.agenda_data.entity_mappers.toAgendaItem
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import com.example.tasky.core.presentation.DateUtils.localDateToStringMMMdyyyyFormat
import com.example.tasky.core.presentation.DateUtils.toMillis
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.util.CredentialsValidator
import com.example.tasky.util.PhotoCompressor
import com.example.tasky.util.PhotoConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
    private val savedStateHandle: SavedStateHandle,
    private val photoCompressor: PhotoCompressor,
    private val photoConverter: PhotoConverter
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaDetailState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaDetailUiState>(AgendaDetailUiState.None)
    val uiState: StateFlow<AgendaDetailUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    val agendaOption = savedStateHandle.toRoute<Screen.AgendaDetail>().agendaOption
    private val isReadOnly = savedStateHandle.toRoute<Screen.AgendaDetail>().isAgendaItemReadOnly

    init {
        updateState(AgendaDetailStateUpdate.UpdateIsReadOnly(isReadOnly))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun updateState(action: AgendaDetailStateUpdate) {
        _state.update {
            when (action) {
                is AgendaDetailStateUpdate.UpdateDate -> it.copy(
                    date = action.newDate.localDateToStringMMMdyyyyFormat(),
                    isDateSelectedFromDatePicker = false
                )

                is AgendaDetailStateUpdate.UpdateTime -> {
                    val updateTime = LocalTime.of(action.hour, action.minute).toMillis()
                    it.copy(task = it.task.copy(time = updateTime))
                }

                is AgendaDetailStateUpdate.UpdateShouldShowDatePicker -> it.copy(
                    shouldShowDatePicker = action.shouldShowDatePicker
                )

                is AgendaDetailStateUpdate.UpdateMonth -> it.copy(month = action.month)
                is AgendaDetailStateUpdate.UpdateShouldShowTimePicker -> it.copy(
                    shouldShowTimePicker = action.shouldShowTimePicker
                )

                is AgendaDetailStateUpdate.UpdateEditType -> it.copy(editType = action.editType)
                is AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown -> it.copy(
                    shouldShowReminderDropdown = action.shouldShowReminderDropdown
                )

                is AgendaDetailStateUpdate.UpdateSelectedReminder -> it.copy(
                    selectedReminder = action.selectedReminder
                )

                is AgendaDetailStateUpdate.UpdateDescription -> it.copy(
                    task = it.task.copy(
                        taskDescription = action.description
                    )
                )

                is AgendaDetailStateUpdate.UpdateTitle -> it.copy(task = it.task.copy(taskTitle = action.title))
                is AgendaDetailStateUpdate.UpdateIsReadOnly -> it.copy(isReadOnly = action.isReadOnly)
                is AgendaDetailStateUpdate.UpdateSelectedAgendaItem -> it.copy(selectedAgendaItem = action.selectedAgendaItem)
                is AgendaDetailStateUpdate.UpdatePhotos -> it.copy(event = it.event.copy(photos = action.photos))
                is AgendaDetailStateUpdate.UpdateAttendees -> it.copy(
                    event = it.event.copy(
                        attendees = action.attendees
                    )
                )

                is AgendaDetailStateUpdate.UpdateAddVisitorEmail -> {
                    val emailErrorStatus =
                        CredentialsValidator.validateEmail(FieldInput(action.email.value).value)
                    it.copy(
                        addVisitorEmail = action.email,
                        emailErrorStatus = emailErrorStatus
                    )
                }

            }
        }
    }

    fun createAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = when (agendaItem) {
                is AgendaItem.Task -> agendaRepository.addTask(agendaItem)
                is AgendaItem.Event -> {
                    val (photos, newAgendaItem) = createNewEvent(agendaItem)
                    agendaRepository.addEvent(newAgendaItem, photos)
                }

                is AgendaItem.Reminder -> agendaRepository.addReminder(agendaItem)
            }

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                    _uiState.update { AgendaDetailUiState.Error(R.string.Could_not_create_agenda_item) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = when (agendaItem) {
                is AgendaItem.Task -> agendaRepository.updateTask(agendaItem)
                is AgendaItem.Event -> {
                    val (photos, newAgendaItem) = prepareUpdatedEvent(agendaItem)
                    agendaRepository.updateEvent(newAgendaItem, photos)
                }
//                is AgendaItem.Reminder -> agendaRepository.updateTask(agendaItem)
                else -> return@launch
            }

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun createNewEvent(agendaItem: AgendaItem.Event): Pair<List<ByteArray>, AgendaItem.Event> {
        val photos = photoConverter.convertPhotosToByteArrays(state.value.event.photos)

        val event = state.value.event
        val newAgendaItem = AgendaItem.Event(
            eventId = UUID.randomUUID().toString(),
            eventTitle = event.eventTitle,
            eventDescription = event.eventDescription,
            from = event.from,
            to = event.to,
            photos = event.photos,
            attendees = agendaItem.attendees + event.attendees,
            isUserEventCreator = true,
            host = agendaItem.host,
            remindAtTime = event.remindAtTime
        )
        return Pair(photos, newAgendaItem)
    }

    private suspend fun prepareUpdatedEvent(agendaItem: AgendaItem.Event): Pair<List<ByteArray>, AgendaItem.Event> {
        val photos = photoConverter.convertPhotosToByteArrays(state.value.event.photos)

        val currentEvent = state.value.event

        val newEvent = _state.value.event.copy(
            eventTitle = agendaItem.eventTitle,
            eventDescription = agendaItem.eventDescription,
            from = agendaItem.from,
            to = agendaItem.to,
            photos = (agendaItem.photos + state.value.event.photos).distinctBy { it.key },
            attendees = (agendaItem.attendees + currentEvent.attendees).distinctBy { it.userId },
            remindAtTime = agendaItem.remindAtTime
        )
        Log.d("DDD - newEvent", "${newEvent.eventId} | ${newEvent.photos}")
        return Pair(photos, newEvent)
    }

    fun loadAgendaItem(agendaItemId: String): AgendaItem? {
        _state.update { it.copy(isLoading = true) }
        var agendaItem: AgendaItem? = null

        viewModelScope.launch {
            agendaItem = when (agendaOption) {
                AgendaOption.EVENT -> {
                    val event = localDatabaseRepository.getEventById(agendaItemId)
                    _state.update { currentState ->
                        currentState.copy(
                            event =  event
                                .toAgendaItem(),
                            isLoading = false
                        )
                    }
                    state.value.event
                }

                AgendaOption.TASK -> {
                    val task = localDatabaseRepository.getTaskById(agendaItemId)
                    _state.update { currentState ->
                        currentState.copy(
                            task = task
                                .toAgendaItem(),
                            isLoading = false
                        )
                    }
                    state.value.task
                }

                AgendaOption.REMINDER -> {
                    val reminder = localDatabaseRepository.getReminderById(agendaItemId)
                    _state.update { currentState ->
                        currentState.copy(
                            reminder = reminder
                                .toAgendaItem(),
                            isLoading = false
                        )
                    }
                    state.value.reminder
                }

                else -> null
            }
            Log.d("DDD - agendaItem", "${agendaItem}")
        }
        return state.value.selectedAgendaItem
    }

    fun getAttendee(email: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            agendaRepository.getAttendee(email)
                .onSuccess { attendeeResponse ->
                    if (attendeeResponse.doesUserExist) {
                        val newAttendee = attendeeResponse.attendee
                        _state.update { currentState ->
                            currentState.copy(
                                event = currentState.event.copy(
                                    attendees = currentState.event.attendees + newAttendee.toAttendee(
                                        eventId = state.value.event.eventId,
                                        remindAt = state.value.event.remindAtTime
                                    )
                                )
                            )
                        }
                        _dialogState.update { DialogState.Hide }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                        _uiState.update { AgendaDetailUiState.Error(R.string.user_does_not_exist) }
                    }
                }.onError {
                    _state.update { it.copy(isLoading = false) }
                    _uiState.update { AgendaDetailUiState.Error(R.string.Unknown_error) }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun showAddVisitorDialog() {
        _dialogState.value = DialogState.Show(
            errorMessage = null
        )
    }

    fun hideAddVisitorDialog() {
        _dialogState.value = DialogState.Hide
    }

    fun handlePhotoCompression(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            photoCompressor.compressPhoto(uri)?.let { compressedData ->
                val newPhoto = Photo(
                    key = UUID.randomUUID().toString(),
                    url = uri.toString()
                )
                updateState(AgendaDetailStateUpdate.UpdatePhotos(state.value.event.photos + newPhoto))
            }
        }
    }

    fun deletePhoto(photoKey: String) {
        val updatedPhotos = _state.value.event.photos.filterNot { it.key == photoKey }
        _state.update { currentState ->
            currentState.copy(event = currentState.event.copy(photos = updatedPhotos))
        }
    }

    sealed class AgendaDetailUiState {
        data object None : AgendaDetailUiState()
        data object Success : AgendaDetailUiState()
        data class Error(val message: Int) : AgendaDetailUiState()
    }
}