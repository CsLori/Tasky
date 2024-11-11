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
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import com.example.tasky.core.presentation.DateUtils.localDateToStringMMMdyyyyFormat
import com.example.tasky.core.presentation.DateUtils.toMillis
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.util.CredentialsValidator
import com.example.tasky.util.PhotoCompressor
import com.example.tasky.util.PhotoConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    private var _errorDialogState = MutableStateFlow<ErrorDialogState>(ErrorDialogState.None)
    val errorDialogState: StateFlow<ErrorDialogState> = _errorDialogState.asStateFlow()

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
                    date = action.date,
                    isDateSelectedFromDatePicker = false
                )

                is AgendaDetailStateUpdate.UpdateEventSecondRowDate -> it.copy(
                    secondRowDate = action.date.localDateToStringMMMdyyyyFormat(),
                    isDateSelectedFromDatePicker = false
                )

                is AgendaDetailStateUpdate.UpdateFromAtTime -> {
                    val updateTime = LocalTime.of(action.hour, action.minute).toMillis()
                    when (it.selectedAgendaItem) {
                        is AgendaItem.Task -> it.copy(task = it.task.copy(time = updateTime))
                        is AgendaItem.Event -> it.copy(event = it.event.copy(from = updateTime))
                        is AgendaItem.Reminder -> it.copy(
                            reminder = it.reminder.copy(
                                time = updateTime
                            )
                        )
                        null -> TODO()
                    }
                }

                is AgendaDetailStateUpdate.UpdateEventSecondRowTime -> {
                    Log.d("DDD - UpdateEventSecondRowTime", "${action.hour} | ${action.minute}")
                    val updateTime = LocalTime.of(action.hour, action.minute).toMillis()
                    it.copy(event = it.event.copy(to = updateTime))
                }

                is AgendaDetailStateUpdate.UpdateShouldShowDatePicker -> it.copy(
                    shouldShowDatePicker = action.shouldShowDatePicker
                )

                is AgendaDetailStateUpdate.UpdateShouldShowSecondRowDatePicker -> it.copy(
                    shouldShowSecondRowDatePicker = action.shouldShowSecondRowDatePicker
                )

                is AgendaDetailStateUpdate.UpdateShouldShowTimePicker -> it.copy(
                    shouldShowTimePicker = action.shouldShowTimePicker
                )

                is AgendaDetailStateUpdate.UpdateShouldShowSecondRowTimePicker -> it.copy(
                    shouldShowSecondRowTimePicker = action.shouldShowTimePicker
                )

                is AgendaDetailStateUpdate.UpdateEditType -> it.copy(editType = action.editType)
                is AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown -> it.copy(
                    shouldShowReminderDropdown = action.shouldShowReminderDropdown
                )

                is AgendaDetailStateUpdate.UpdateSelectedReminder -> it.copy(
                    selectedReminder = action.selectedReminder
                )

                is AgendaDetailStateUpdate.UpdateDescription ->
                    when (it.selectedAgendaItem) {
                        is AgendaItem.Task -> it.copy(task = it.task.copy(taskDescription = action.description))
                        is AgendaItem.Event -> it.copy(event = it.event.copy(eventDescription = action.description))
                        is AgendaItem.Reminder -> it.copy(
                            reminder = it.reminder.copy(
                                reminderDescription = action.description
                            )
                        )

                        null -> TODO()
                    }

                is AgendaDetailStateUpdate.UpdateTitle ->
                    when (it.selectedAgendaItem) {
                        is AgendaItem.Task -> it.copy(task = it.task.copy(taskTitle = action.title))
                        is AgendaItem.Event -> it.copy(event = it.event.copy(eventTitle = action.title))
                        is AgendaItem.Reminder -> it.copy(reminder = it.reminder.copy(reminderTitle = action.title))
                        null -> TODO()
                    }

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

                is AgendaDetailStateUpdate.UpdateVisitorFilter -> it.copy(visitorFilter = action.filter)
                is AgendaDetailStateUpdate.UpdateRemindAtTime -> {
                    when (it.selectedAgendaItem) {
                        is AgendaItem.Task -> it.copy(task = it.task.copy(remindAtTime = action.remindAtTime))
                        is AgendaItem.Event -> it.copy(event = it.event.copy(remindAtTime = action.remindAtTime))
                        is AgendaItem.Reminder -> it.copy(reminder = it.reminder.copy(remindAtTime = action.remindAtTime))
                        null -> TODO()
                    }
                }

                is AgendaDetailStateUpdate.UpdateSortDate -> {
                    when (it.selectedAgendaItem) {
                        is AgendaItem.Task -> it.copy(task = it.task.copy(time = action.sortDate))
                        is AgendaItem.Event -> it.copy(event = it.event.copy(from = action.sortDate))
                        is AgendaItem.Reminder -> it.copy(reminder = it.reminder.copy(time = action.sortDate))
                        null -> TODO()
                    }
                }

                is AgendaDetailStateUpdate.UpdateSecondRowToDate -> {
                    it.copy(event = it.event.copy(to = action.toDate))
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
                    val (photos, newAgendaItem) = createNewEvent()
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
                    _dialogState.update { DialogState.ShowError }
                    _errorDialogState.update {
                        ErrorDialogState.AgendaItemError(
                            UiText.StringResource(
                                R.string.Could_not_create_agenda_item
                            )
                        )
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = when (agendaItem) {
                is AgendaItem.Task -> {
                    val newTask = prepareUpdatedTask(agendaItem)
                    agendaRepository.updateTask(newTask)
                }

                is AgendaItem.Event -> {
                    val (photos, newEvent) = prepareUpdatedEvent(agendaItem)
                    agendaRepository.updateEvent(newEvent, photos)
                }

                is AgendaItem.Reminder -> {
                    val newReminder = prepareUpdatedReminder(agendaItem)
                    agendaRepository.updateReminder(newReminder)
                }
            }

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                }

                is Error -> {
                    val message = if ( result.error == TaskyError.NetworkError.IMAGE_TOO_LARGE) {
                        UiText.StringResource(
                            R.string.Image_too_large
                        )
                    } else {
                        UiText.StringResource(
                            R.string.Something_went_wrong
                        )
                    }
                    _uiState.update { AgendaDetailUiState.None }
                    _dialogState.update { DialogState.ShowError }
                    _errorDialogState.update {
                        ErrorDialogState.AgendaItemError(
                            UiText.StringResource(
                                R.string.Something_went_wrong
                            )
                        )
                    }

                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun prepareUpdatedTask(agendaItem: AgendaItem.Task): AgendaItem.Task {
        val currentState = state.value.task
        val newTask = _state.value.task.copy(
            taskTitle = currentState.taskTitle,
            taskDescription = currentState.taskDescription,
            time = currentState.time,
            isDone = currentState.isDone,
            remindAtTime = currentState.remindAtTime
        )
        return newTask
    }

    private fun prepareUpdatedReminder(agendaItem: AgendaItem.Reminder): AgendaItem.Reminder {
        val currentState = state.value.reminder
        val newReminder = _state.value.reminder.copy(
            reminderTitle = currentState.reminderTitle,
            reminderDescription = currentState.reminderDescription,
            time = currentState.time,
            remindAtTime = currentState.remindAtTime
        )
        return newReminder
    }

    private suspend fun createNewEvent(): Pair<List<ByteArray>, AgendaItem.Event> {
        val currentState = state.value.event
        val photosJob = viewModelScope.async(Dispatchers.IO) {
            photoConverter.convertPhotosToByteArrays(currentState.photos)
        }
        val photos = photosJob.await()

        val eventId = UUID.randomUUID().toString()

        val loggedInUserResult = agendaRepository.getLoggedInUserDetails()
        val loggedInAttendee: Attendee? = when (loggedInUserResult) {
            is Success -> {
                Attendee(
                    userId = loggedInUserResult.data.userId,
                    name = loggedInUserResult.data.fullName,
                    email = loggedInUserResult.data.email,
                    eventId = eventId,
                    isGoing = true,
                    remindAt = currentState.remindAtTime,
                    isCreator = true
                )
            }

            is Error -> {
                Log.d(
                    "LoadingUserError",
                    "Error fetching user details: ${loggedInUserResult.error}"
                )
                null
            }
        }

        val newAgendaItem = AgendaItem.Event(
            eventId = eventId,
            eventTitle = currentState.eventTitle,
            eventDescription = currentState.eventDescription,
            from = currentState.from,
            to = currentState.to,
            photos = currentState.photos,
            attendees = currentState.attendees + listOfNotNull(loggedInAttendee),
            isUserEventCreator = true,
            host = currentState.host,
            remindAtTime = currentState.remindAtTime
        )
        return Pair(photos, newAgendaItem)
    }

    private suspend fun prepareUpdatedEvent(agendaItem: AgendaItem.Event): Pair<List<ByteArray>, AgendaItem.Event> {
        val currentState = state.value.event
        val photosJob = viewModelScope.async(Dispatchers.IO) {
            photoConverter.convertPhotosToByteArrays(currentState.photos)
        }
        val photos = photosJob.await()

        val newEvent = _state.value.event.copy(
            eventTitle = currentState.title,
            eventDescription = currentState.description,
            from = currentState.from,
            to = currentState.to,
            photos = (agendaItem.photos + currentState.photos).distinctBy { it.key },
            attendees = (agendaItem.attendees + currentState.attendees).distinctBy { it.userId },
            remindAtTime = currentState.remindAtTime,
        )
        return Pair(photos, newEvent)
    }

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = when (agendaItem) {
                is AgendaItem.Task -> agendaRepository.deleteTask(agendaItem)
                is AgendaItem.Event -> agendaRepository.deleteEvent(agendaItem)
                is AgendaItem.Reminder -> agendaRepository.deleteReminder(agendaItem)
            }

            result.onSuccess {
                //Maybe some success message here
//                    _uiState.update { AgendaDetailUiState.Error(R.string.Agenda_item_was_successfully_deleted) }

            }.onError {
                _dialogState.update { DialogState.ShowError }
                _errorDialogState.update {
                    ErrorDialogState.AgendaItemError(
                        UiText.StringResource(
                            R.string.Could_not_create_agenda_item
                        )
                    )
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun deleteAttendee(attendee: Attendee) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            agendaRepository.deleteAttendee(attendee.eventId)
                .onSuccess {

                }
                .onError {
                    _dialogState.update { DialogState.ShowError }
                    _errorDialogState.update {
                        ErrorDialogState.AttendeeError(
                            UiText.StringResource(
                                R.string.Something_went_wrong
                            )
                        )
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun loadAgendaItem(agendaItemId: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            when (agendaOption) {
                AgendaOption.EVENT -> {
                    agendaRepository.getEventById(agendaItemId)
                        .onSuccess { event ->
                            _state.update { currentState ->
                                currentState.copy(
                                    selectedAgendaItem = event,
                                    event = event,
                                    isLoading = false
                                )
                            }
                        }
                        .onError {
                            _state.update { it.copy(isLoading = false) }
                        }
                }

                AgendaOption.TASK -> {
                    agendaRepository.getTaskById(agendaItemId)
                        .onSuccess { task ->
                            _state.update { currentState ->
                                currentState.copy(
                                    selectedAgendaItem = task,
                                    task = task,
                                    isLoading = false
                                )
                            }
                        }
                        .onError {
                            _state.update { it.copy(isLoading = false) }
                        }
                }

                AgendaOption.REMINDER -> {
                    agendaRepository.getReminderById(agendaItemId)
                        .onSuccess { reminder ->
                            _state.update { currentState ->
                                currentState.copy(
                                    selectedAgendaItem = reminder,
                                    reminder = reminder,
                                    isLoading = false
                                )
                            }
                        }
                        .onError {
                            _state.update { it.copy(isLoading = false) }
                            _dialogState.update { DialogState.ShowError }
                            _errorDialogState.update {
                                ErrorDialogState.AgendaItemError(
                                    UiText.StringResource(
                                        R.string.Agenda_item_failed
                                    )
                                )
                            }
                        }
                }
            }
        }
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
                        _dialogState.update { DialogState.ShowError }
                        _errorDialogState.update {
                            ErrorDialogState.AttendeeError(
                                UiText.StringResource(
                                    R.string.user_does_not_exist
                                )
                            )
                        }
                    }
                }.onError {
                    _state.update { it.copy(isLoading = false) }
                    _dialogState.update { DialogState.ShowError }
                    _errorDialogState.update {
                        ErrorDialogState.AttendeeError(
                            UiText.StringResource(
                                R.string.Unknown_error
                            )
                        )
                    }
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

    fun hideErrorDialog() {
        _dialogState.value = DialogState.HideError
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

    sealed class ErrorDialogState {
        data object None : ErrorDialogState()
        data class GeneralError(val message: UiText) : ErrorDialogState()
        data class AttendeeError(val message: UiText) : ErrorDialogState()
        data class AgendaItemError(val message: UiText) : ErrorDialogState()
    }

    sealed class AgendaDetailUiState {
        data object None : AgendaDetailUiState()
        data object Success : AgendaDetailUiState()
    }
}