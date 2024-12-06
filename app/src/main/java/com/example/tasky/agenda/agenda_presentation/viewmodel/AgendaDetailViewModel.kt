package com.example.tasky.agenda.agenda_presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.agenda.agenda_data.dto_mappers.toAttendee
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.domain.Result.Error
import com.example.tasky.core.domain.Result.Success
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.util.CredentialsValidator
import com.example.tasky.util.Logger
import com.example.tasky.util.PhotoCompressor
import com.example.tasky.util.PhotoConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val photoCompressor: PhotoCompressor,
    private val photoConverter: PhotoConverter,
    private val alarmSchedulerService: AlarmScheduler,
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaDetailState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaDetailUiState>(AgendaDetailUiState.None)
    val uiState: StateFlow<AgendaDetailUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _errorDialogState = MutableStateFlow<ErrorDialogState>(ErrorDialogState.None)
    val errorDialogState: StateFlow<ErrorDialogState> = _errorDialogState.asStateFlow()

    private var _sessionCount = MutableStateFlow<Int>(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _hasSeenNotificationPrompt = MutableStateFlow(false)
    val hasSeenNotificationPrompt: StateFlow<Boolean> = _hasSeenNotificationPrompt

    val agendaOption = savedStateHandle.toRoute<Screen.AgendaDetail>().agendaOption
    private val isReadOnly = savedStateHandle.toRoute<Screen.AgendaDetail>().isAgendaItemReadOnly

    private var deletedPhotos: MutableList<String> = mutableListOf()

    private fun getSessionCount() {
        viewModelScope.launch {
            _sessionCount.value = userPrefsRepository.getSessionCount()
        }
    }

    fun setHasSeenNotificationPrompt(hasSeenNotificationPrompt: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.updateHasSeenNotificationPrompt(hasSeenNotificationPrompt)
            _hasSeenNotificationPrompt.value = hasSeenNotificationPrompt
        }
    }

//    val networkStatus: StateFlow<NetworkStatus> = networkConnectivityService.networkStatus.stateIn(
//        initialValue = NetworkStatus.Unknown,
//        scope = viewModelScope,
//        started = WhileSubscribed(5000)
//    )

    init {
        updateState(AgendaDetailStateUpdate.UpdateIsReadOnly(isReadOnly))
        getSessionCount()
    }

    fun updateState(action: AgendaDetailStateUpdate) {
        when (action) {
            is AgendaDetailStateUpdate.UpdateEndTime -> {
                _state.update { currentState ->
                    val eventDetails =
                        (currentState.details as? AgendaItemDetails.Event)
                            ?: return@update currentState
                    currentState.copy(
                        details = eventDetails.copy(
                            toTime = LocalDateTime.of(
                                /* date = */ eventDetails.toTime.toLocalDate(),
                                /* time = */ LocalTime.of(action.hour, action.minute)
                            )
                        )
                    )
                }
            }

            is AgendaDetailStateUpdate.UpdateEndDay -> {
                _state.update { currentState ->
                    val eventDetails =
                        (currentState.details as? AgendaItemDetails.Event)
                            ?: return@update currentState
                    currentState.copy(
                        details = eventDetails.copy(
                            toTime = LocalDateTime.of(
                                /* date = */ LocalDate.of(action.year, action.month, action.day),
                                /* time = */ eventDetails.toTime.toLocalTime()
                            )
                        ),
                        isDateSelectedFromDatePicker = false
                    )
                }
            }

            else -> Unit
        }
        _state.update { currentState ->
            when (action) {
                is AgendaDetailStateUpdate.UpdateStartTime -> currentState.copy(
                    time = currentState.time.withHour(action.hour)?.withMinute(action.minute)
                        ?: LocalDateTime.now(),
                    isDateSelectedFromDatePicker = false
                )

                is AgendaDetailStateUpdate.UpdateStartDay -> currentState.copy(
                    time = currentState.time.withYear(action.year)?.withMonth(action.month)
                        ?.withDayOfMonth(action.day)
                        ?: LocalDateTime.now()
                )

                is AgendaDetailStateUpdate.UpdateEditType -> currentState.copy(editType = action.editType)
                is AgendaDetailStateUpdate.UpdateSelectedReminder -> currentState.copy(
                    selectedReminder = action.selectedReminder
                )

                is AgendaDetailStateUpdate.UpdateDescription -> currentState.copy(description = action.description)
                is AgendaDetailStateUpdate.UpdateTitle -> currentState.copy(title = action.title)
                is AgendaDetailStateUpdate.UpdateIsReadOnly -> currentState.copy(isReadOnly = action.isReadOnly)
                is AgendaDetailStateUpdate.UpdateSelectedAgendaItem -> currentState.copy(
                    id = action.selectedAgendaItem.id,
                    title = action.selectedAgendaItem.title,
                    description = action.selectedAgendaItem.description,
                    time = action.selectedAgendaItem.time,
                    remindAt = action.selectedAgendaItem.remindAt,
                    details = action.selectedAgendaItem.details,
                )

                is AgendaDetailStateUpdate.UpdatePhotos -> currentState.copy(
                    details = (currentState.details as? AgendaItemDetails.Event?)?.copy(
                        photos = action.photos
                    )
                )

                is AgendaDetailStateUpdate.UpdateAttendees -> currentState.copy(
                    details = (currentState.details as? AgendaItemDetails.Event?)?.copy(attendees = action.attendees)
                )

                is AgendaDetailStateUpdate.UpdateAddVisitorEmail -> {
                    val emailErrorStatus =
                        CredentialsValidator.validateEmail(FieldInput(action.email.value).value)
                    currentState.copy(
                        addVisitorEmail = action.email,
                        emailErrorStatus = emailErrorStatus
                    )
                }

                is AgendaDetailStateUpdate.UpdateVisitorFilter -> currentState.copy(visitorFilter = action.filter)
                is AgendaDetailStateUpdate.UpdateRemindAtTime -> currentState.copy(remindAt = action.remindAtTime)
                else -> currentState
            }
        }
    }

    fun createAgendaItem() {
        _state.update { it.copy(isLoading = true) }
        val details = _state.value.details ?: return
        val agendaItem = AgendaItem(
            id = _state.value.id,
            title = state.value.title,
            description = state.value.description,
            time = state.value.time,
            remindAt = state.value.remindAt,
            details = details
        )

        viewModelScope.launch {
            val result = when (agendaItem.details) {
                is AgendaItemDetails.Task -> {
                    val newTask = createTask()
                    agendaRepository.addTask(newTask)
                }

                is AgendaItemDetails.Event -> {
                    val (photos, newAgendaItem) = createNewEvent()
                    agendaRepository.addEvent(newAgendaItem, photos)
                }

                is AgendaItemDetails.Reminder -> {
                    val newReminder = createReminder()
                    agendaRepository.addReminder(newReminder)
                }
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

    fun updateAgendaItem() {
        _state.update { it.copy(isLoading = true) }
        val details = _state.value.details ?: return
        val agendaItem = AgendaItem(
            id = _state.value.id,
            title = state.value.title,
            description = state.value.description,
            time = state.value.time,
            remindAt = state.value.remindAt,
            details = details
        )
        viewModelScope.launch {
            val result = when (agendaItem.details) {
                is AgendaItemDetails.Task -> {
                    val newTask = prepareUpdatedTask(agendaItem)
                    agendaRepository.updateTask(newTask)
                }

                is AgendaItemDetails.Event -> {
                    val (photos, newEvent) = prepareUpdatedEvent(agendaItem)
                    agendaRepository.updateEvent(newEvent, photos, deletedPhotos.toList())
                }

                is AgendaItemDetails.Reminder -> {
                    val newReminder = prepareUpdatedReminder(agendaItem)
                    agendaRepository.updateReminder(newReminder)
                }
            }

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                }

                is Error -> {
                    val message = if (result.error == TaskyError.NetworkError.IMAGE_TOO_LARGE) {
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
                            message
                        )
                    }

                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun createTask(): AgendaItem {
        return AgendaItem(
            id = UUID.randomUUID().toString(),
            title = state.value.title ?: "Task",
            description = state.value.description ?: "New task",
            time = state.value.time,
            details = AgendaItemDetails.Task(isDone = false),
            remindAt = state.value.remindAt
        )
    }

    private fun createReminder(): AgendaItem {
        return AgendaItem(
            id = UUID.randomUUID().toString(),
            title = state.value.title ?: "Reminder",
            description = state.value.description ?: "New reminder",
            time = state.value.time,
            details = AgendaItemDetails.Reminder,
            remindAt = state.value.remindAt
        )
    }

    private fun prepareUpdatedTask(agendaItem: AgendaItem): AgendaItem {
        val newTask = agendaItem.copy(
            id = state.value.id,
            title = state.value.title,
            description = state.value.description,
            time = state.value.time,
            remindAt = state.value.remindAt
        )

        return newTask
    }

    private fun prepareUpdatedReminder(agendaItem: AgendaItem): AgendaItem {
        val newReminder = agendaItem.copy(
            id = state.value.id,
            title = state.value.title,
            description = state.value.description,
            time = state.value.time,
            remindAt = state.value.remindAt
        )
        return newReminder
    }

    private suspend fun createNewEvent(): Pair<List<ByteArray>, AgendaItem> {
        val currentState = _state.value.details as? AgendaItemDetails.Event
            ?: throw IllegalStateException("Selected agenda item is not an Event")

        val photosJob = viewModelScope.async(Dispatchers.IO) {
            photoConverter.convertPhotosToByteArrays((currentState as AgendaItemDetails.Event).photos)
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
                    remindAt = _state.value.remindAt.toLong(),
                    isCreator = true
                )
            }

            is Error -> {
                Logger.d(
                    "LoadingUserError",
                    "Error fetching user details: ${loggedInUserResult.error}"
                )
                null
            }
        }

        val newAgendaItem = AgendaItem(
            id = eventId,
            title = _state.value.title ?: "Event",
            description = _state.value.description ?: "New event",
            time = _state.value.time,
            details = AgendaItemDetails.Event(
                toTime = currentState.toTime,
                photos = currentState.photos,
                attendees = currentState.attendees + listOfNotNull(loggedInAttendee),
                isUserEventCreator = true,
                host = currentState.host ?: loggedInAttendee?.name ?: "",
            ),
            remindAt = _state.value.remindAt
        )

        return Pair(photos, newAgendaItem)
    }

    private suspend fun prepareUpdatedEvent(agendaItem: AgendaItem): Pair<List<ByteArray>, AgendaItem> {
        val currentState = state.value.details as? AgendaItemDetails.Event
            ?: throw IllegalStateException("Current details are not of type Event")
        val photosJob = viewModelScope.async(Dispatchers.IO) {
            photoConverter.convertPhotosToByteArrays((state.value.details as AgendaItemDetails.Event).photos)
        }
        val photosByteArray = photosJob.await()
        val attendees = (state.value.details as? AgendaItemDetails.Event)?.attendees ?: emptyList()
        val addedPhotos = (state.value.details as AgendaItemDetails.Event).photos
        val agendaEventDetails = agendaItem.details as? AgendaItemDetails.Event
            ?: throw IllegalArgumentException("Agenda item details must be of type Event")


        val updatedAttendees = attendees.map { attendee ->
            attendee.copy(eventId = agendaItem.id)
        }

        val newEvent = agendaItem.copy(
            id = state.value.id,
            title = state.value.title,
            description = state.value.description,
            time = state.value.time,
            details = AgendaItemDetails.Event(
                toTime = currentState.toTime,
                photos = (agendaEventDetails.photos + addedPhotos).distinctBy { it.key },
                attendees = (agendaEventDetails.attendees + updatedAttendees).distinctBy { it.userId },
                isUserEventCreator = currentState.isUserEventCreator,
                host = currentState.host,
            ),
            remindAt = state.value.remindAt,
        )
        return Pair(photosByteArray, newEvent)
    }

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // TODO The problem with this approach is that is does not scale well
            //  This when statement will grow as more agenda items get added (not very applicable for Tasky
            //  since there aren't many more types of Agenda items) and for each new Type, you'll increase
            //  the amount of functions the repository has.
            //  Instead, have a single functions to delete an agenda item and let the parameters you pass
            //  tell the implemenatation which type of agenda to delete. That way, you also unburden your ViewModel a bit

//            agendaRepository.deleteAgenda(id= agendaItem.id, type = AGENDA_TYPE.TASK)
            // Or just use this approach if you embed the type into the ID
//            agendaRepository.deleteAgenda(id= agendaItem.id)
            val result = when (agendaItem.details) {
                is AgendaItemDetails.Task -> agendaRepository.deleteTask(agendaItem.id)
                is AgendaItemDetails.Event -> agendaRepository.deleteEvent(agendaItem.id)
                is AgendaItemDetails.Reminder -> agendaRepository.deleteReminder(agendaItem.id)
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
                    if (attendee.isCreator) {
                        // TODO: Use ID to delete agenda item, not entire object
                        //  If needed, also pass an enum to uniquely identify the agenda item (task vs reminder vs event)
                        //  Even better, you can have a unique prefix to the ID that you can later use to identify
                        //  what agenda item the ID refers to (very useful IMO)
//                        state.value.selectedAgendaItem?.let { safeAgendaItem ->
//                            deleteAgendaItem(
//                                safeAgendaItem
//                            )
//                        }
                    }
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
            val result = when (agendaOption) {
                AgendaOption.EVENT -> agendaRepository.getEventById(agendaItemId)
                AgendaOption.TASK -> agendaRepository.getTaskById(agendaItemId)
                AgendaOption.REMINDER -> agendaRepository.getReminderById(agendaItemId)
            }

            result.onSuccess { agendaItem ->
                _state.update { currentState ->
                    currentState.copy(
                        id = agendaItem.id,
                        title = agendaItem.title,
                        description = agendaItem.description,
                        time = agendaItem.time,
                        remindAt = agendaItem.remindAt,
                        details = agendaItem.details,
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

    fun getAttendee(email: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            agendaRepository.getAttendee(email)
                .onSuccess { attendeeResponse ->
                    if (attendeeResponse.doesUserExist) {
                        val newAttendee = attendeeResponse.attendee
                        _state.update { currentState ->
                            val currentDetails =
                                currentState.details as? AgendaItemDetails.Event
                                    ?: return@update currentState // Exit if not an Event

                            val updatedAttendees =
                                currentDetails.attendees + newAttendee.toAttendee(
                                    eventId = state.value.id,
                                    remindAt = state.value.remindAt.toLong(),
                                    isCreator = false
                                )
                            currentState.copy(
                                details = currentDetails.copy(
                                    attendees = updatedAttendees
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
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            photoCompressor.compressPhoto(uri)?.let { compressedData ->
                if (compressedData.size < 1_000_000){
                    val newPhoto = Photo(
                        key = UUID.randomUUID().toString(),
                        url = uri.toString()
                    )
                    updateState(
                        AgendaDetailStateUpdate.UpdatePhotos(
                            ((state.value.details as? AgendaItemDetails.Event?)?.photos
                                ?: emptyList()) + newPhoto
                        )
                    )
                }
                else {
                    Timber.d("DDD - Image too large hence it was skipped!")
                    _errorDialogState.update { ErrorDialogState.GeneralError(UiText.StringResource(R.string.Image_too_large)) }
                }
            }
        }
    }

    fun deletePhoto(photoKey: String) {
        val updatedPhotos =
            (state.value.details as? AgendaItemDetails.Event)?.photos?.filterNot { it.key == photoKey }
                ?: emptyList()
        val deletedPhoto =
            (state.value.details as? AgendaItemDetails.Event)?.photos?.find { it.key == photoKey }
        deletedPhotos.add(deletedPhoto?.key ?: "")
        _state.update { currentState ->
            currentState.copy(
                details = (currentState.details as? AgendaItemDetails.Event)?.copy(
                    photos = updatedPhotos
                )
            )
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