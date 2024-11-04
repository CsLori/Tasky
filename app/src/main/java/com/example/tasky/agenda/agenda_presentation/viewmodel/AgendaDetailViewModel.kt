@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.viewmodel

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
    private val photoCompressor: PhotoCompressor
) : ViewModel() {

    private var _state = MutableStateFlow(AgendaDetailState())
    val state = _state.asStateFlow()

    private var _uiState = MutableStateFlow<AgendaDetailUiState>(AgendaDetailUiState.None)
    val uiState: StateFlow<AgendaDetailUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    val agendaOption = savedStateHandle.get<AgendaOption>("agendaOption") ?: AgendaOption.EVENT
    private val isReadOnly = savedStateHandle.toRoute<Screen.AgendaDetail>().isAgendaItemReadOnly
    private val photoIdFromPhotoScreen = savedStateHandle.get<String>("photoId")

    init {
        updateState(AgendaDetailStateUpdate.UpdateIsReadOnly(isReadOnly))

        photoIdFromPhotoScreen?.let { safeId -> deletePhoto(safeId) }
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

    fun createTask() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result = agendaRepository.addTask(
                task = state.value.task
            )
            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                    Success(Unit)
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                    Error(result.error)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateTask(task: AgendaItem.Task) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = agendaRepository.updateTask(task)

            when (result) {
                is Success -> {
                    _uiState.update { AgendaDetailUiState.Success }
                    Success(Unit)
                }

                is Error -> {
                    _uiState.update { AgendaDetailUiState.None }
                    Error(result.error)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun loadTask(taskId: String): AgendaItem {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            val taskEntity = localDatabaseRepository.getTaskById(taskId)

            _state.update { currentState ->
                currentState.copy(
                    task = taskEntity.toAgendaItem(),
                    isLoading = false
                )
            }
        }
        return state.value.task
    }

    fun getAttendee(email: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            agendaRepository.getAttendee(email)
                .onSuccess { attendeeResponse ->
                    if (attendeeResponse.doesUserExist) {
                        _state.update { currentState ->
                            currentState.copy(
                                event = currentState.event.copy(
                                    attendees = currentState.event.attendees + attendeeResponse.attendee.toAttendee()
                                )
                            )
                        }
                        _dialogState.update { DialogState.Hide }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }.onError {
                    _state.update { it.copy(isLoading = false) }
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
    }
}