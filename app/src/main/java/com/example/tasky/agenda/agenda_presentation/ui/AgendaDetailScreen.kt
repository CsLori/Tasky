@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.components.AddPhotosSection
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemDescription
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemMainHeader
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemTitle
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.components.SetReminderRow
import com.example.tasky.agenda.agenda_presentation.components.TimeAndDateRow
import com.example.tasky.agenda.agenda_presentation.components.TimeAndDateSecondRow
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.VisitorFilter
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.components.AddVisitorDialog
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.presentation.components.ErrorDialog
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.getInitials


@Composable
internal fun AgendaDetailScreen(
    agendaDetailViewModel: AgendaDetailViewModel,
    onNavigateToAgendaScreen: () -> Unit,
    onClose: () -> Unit,
    onEditPressed: () -> Unit,
    agendaItemId: String? = null,
    onNavigateToSelectedPhoto: (String?) -> Unit
) {
    val state = agendaDetailViewModel.state.collectAsStateWithLifecycle().value
    val uiState = agendaDetailViewModel.uiState.collectAsStateWithLifecycle().value
    val dialogState = agendaDetailViewModel.dialogState.collectAsStateWithLifecycle().value
    val errorDialogState =
        agendaDetailViewModel.errorDialogState.collectAsStateWithLifecycle().value

    LaunchedEffect(agendaItemId) {
        if (agendaItemId == null) {
            agendaDetailViewModel.updateState(
                AgendaDetailStateUpdate.UpdateSelectedAgendaItem(
                    when (agendaDetailViewModel.agendaOption) {
                        AgendaOption.TASK -> state.task
                        AgendaOption.EVENT -> state.event
                        AgendaOption.REMINDER -> state.reminder
                    }
                )
            )
        } else {
            agendaDetailViewModel.loadAgendaItem(agendaItemId)
            agendaDetailViewModel.updateState(
                AgendaDetailStateUpdate.UpdateSelectedAgendaItem(
                    state.selectedAgendaItem
                )
            )
        }
    }

    AgendaDetailContent(
        state = state,
        uiState = uiState,
        onUpdateState = { agendaDetailViewModel.updateState(it) },
        onAction = { action ->
            when (action) {
                AgendaDetailAction.OnClosePressed -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnCreateSuccess -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnEditRowPressed -> onEditPressed()
                AgendaDetailAction.OnSavePressed -> {
                    if (agendaItemId == null) {
                        state.selectedAgendaItem?.let { agendaDetailViewModel.createAgendaItem(it) }
                    } else {
                        agendaDetailViewModel.updateAgendaItem(
                            state.selectedAgendaItem ?: state.task
                        )
                    }
                    onNavigateToAgendaScreen()
                }

                AgendaDetailAction.OnEnableEditPressed -> agendaDetailViewModel.updateState(
                    AgendaDetailStateUpdate.UpdateIsReadOnly(false)
                )

                AgendaDetailAction.OnAddVisitorPressed -> {
                    state.addVisitorEmail?.let { agendaDetailViewModel.getAttendee(it.value) }
                }

                is AgendaDetailAction.OnPhotoCompress -> {
                    agendaDetailViewModel.handlePhotoCompression(action.uri)
                }

                is AgendaDetailAction.OnPhotoPressed -> onNavigateToSelectedPhoto(action.key)

                is AgendaDetailAction.OnVisitorFilterChanged -> when (state.visitorFilter) {
                    VisitorFilter.ALL -> state.event.attendees
                    VisitorFilter.GOING -> state.event.attendees.filter { it.isGoing }
                    VisitorFilter.NOT_GOING -> state.event.attendees.filterNot { it.isGoing }
                }

                is AgendaDetailAction.OnDeleteAgendaItem -> {
                    agendaDetailViewModel.deleteAgendaItem(action.agendaItem)
                    onNavigateToAgendaScreen()
                }

                is AgendaDetailAction.OnDeleteAttendee -> {
                    agendaDetailViewModel.deleteAttendee(action.attendee)
                }
            }
        },
        agendaItemId = agendaItemId,
        agendaItem = state.selectedAgendaItem,
        onUpdatePhotos = { photos ->
            agendaDetailViewModel.updateState(AgendaDetailStateUpdate.UpdatePhotos(photos))
        },
        onShowDialog = { agendaDetailViewModel.showAddVisitorDialog() },
        onDialogDismiss = { agendaDetailViewModel.hideAddVisitorDialog() },
        onErrorDialogDismiss = { agendaDetailViewModel.hideErrorDialog() },
        dialogState = dialogState,
        errorDialogState = errorDialogState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AgendaDetailContent(
    state: AgendaDetailState,
    uiState: AgendaDetailViewModel.AgendaDetailUiState,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    agendaItemId: String?,
    agendaItem: AgendaItem?,
    onUpdatePhotos: (List<Photo>) -> Unit,
    onShowDialog: () -> Unit,
    onDialogDismiss: () -> Unit,
    dialogState: DialogState,
    errorDialogState: AgendaDetailViewModel.ErrorDialogState,
    onErrorDialogDismiss: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val getContent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }
    val context = LocalContext.current

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        when (uiState) {
            AgendaDetailViewModel.AgendaDetailUiState.Success -> {
                onAction(AgendaDetailAction.OnCreateSuccess)
            }

            AgendaDetailViewModel.AgendaDetailUiState.None -> {
                if (dialogState is DialogState.ShowError) {
                    when (errorDialogState) {
                        is AgendaDetailViewModel.ErrorDialogState.AgendaItemError -> {
                            ErrorDialog(
                                label = errorDialogState.message.asString() ?: "",
                                displayCloseIcon = false,
                                positiveButtonText = stringResource(R.string.OK),
                                positiveOnClick = { onErrorDialogDismiss() },
                            )
                        }

                        is AgendaDetailViewModel.ErrorDialogState.AttendeeError -> {
                            ErrorDialog(
                                label = errorDialogState.message.asString() ?: "",
                                displayCloseIcon = false,
                                positiveButtonText = stringResource(R.string.OK),
                                positiveOnClick = { onErrorDialogDismiss() },
                            )
                        }

                        is AgendaDetailViewModel.ErrorDialogState.GeneralError -> {
                            ErrorDialog(
                                label = errorDialogState.message.asString() ?: "",
                                displayCloseIcon = false,
                                positiveButtonText = stringResource(R.string.OK),
                                positiveOnClick = { onErrorDialogDismiss() },
                            )
                        }

                        AgendaDetailViewModel.ErrorDialogState.None -> {}
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Header(
                        onSavePressed = { onAction(AgendaDetailAction.OnSavePressed) },
                        onClosePressed = { onAction(AgendaDetailAction.OnClosePressed) },
                        onEnableEditPressed = { onAction(AgendaDetailAction.OnEnableEditPressed) },
                        agendaItemId = agendaItemId,
                        isReadOnly = state.isReadOnly
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = dimensions.cornerRadius30dp,
                        topEnd = dimensions.cornerRadius30dp
                    ),
                    color = colors.white,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 70.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp)
                            .verticalScroll(rememberScrollState())

                    ) {
                        MainContent(
                            state = state,
                            agendaItem = agendaItem,
                            onUpdateState = onUpdateState,
                            onAction = onAction,
                            onAddPhotosPressed = {
                                if (!state.isReadOnly) {
                                    getContent.launch("image/*")
                                }
                            },
                            selectedImageUri = selectedImageUri,
                            onShowDialog = onShowDialog,
                            onDialogDismiss = onDialogDismiss,
                            dialogState = dialogState,
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    state: AgendaDetailState,
    agendaItem: AgendaItem?,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    onAddPhotosPressed: () -> Unit,
    selectedImageUri: Uri?,
    onShowDialog: () -> Unit,
    onDialogDismiss: () -> Unit,
    dialogState: DialogState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.default16dp)
    ) {
        AgendaItemMainHeader(agendaItem)

        AgendaItemTitle(agendaItem, onUpdateState, onAction, state)

        AgendaItemDescription(agendaItem, onUpdateState, onAction, state)

        if (agendaItem is AgendaItem.Event) {
            TimeAndDateRow(
                agendaItem = agendaItem,
                text = stringResource(R.string.from),
                onUpdateState = onUpdateState,
                state = state,

                )

            DefaultHorizontalDivider()

            TimeAndDateSecondRow(
                agendaItem = agendaItem,
                text = stringResource(R.string.to),
                onUpdateState = onUpdateState,
                state = state,
            )
        } else {
            if (agendaItem != null) {
                TimeAndDateRow(
                    agendaItem = agendaItem,
                    text = stringResource(R.string.at),
                    onUpdateState = onUpdateState,
                    state = state,
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (agendaItem is AgendaItem.Event) {
            AddPhotosSection(
                isReadOnly = state.isReadOnly,
                onAddPhotos = onAddPhotosPressed,
                selectedImageUri = selectedImageUri,
                onUpdateState = onUpdateState,
                photos = state.event.photos,
                onAction = onAction
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.default16dp)
    ) {
        DefaultHorizontalDivider()

        SetReminderRow(onUpdateState, state)

        if (agendaItem is AgendaItem.Event) {
            VisitorsSection(
                visitors = state.event.attendees,
                onVisitorStatusChanged = { onAction(AgendaDetailAction.OnVisitorFilterChanged) },
                onShowDialog = onShowDialog,
                onDialogDismiss = onDialogDismiss,
                dialogState = dialogState,
                email = state.addVisitorEmail ?: FieldInput(""),
                emailErrorStatus = state.emailErrorStatus ?: ErrorStatus(false),
                onUpdateState = onUpdateState,
                onAction = onAction,
                visitorFilter = state.visitorFilter,
                isReadOnly = state.isReadOnly
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensions.large32dp, bottom = dimensions.default16dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (agendaItem !is AgendaItem.Event) DefaultHorizontalDivider()

                Spacer(modifier = Modifier.height(dimensions.small8dp))
                Text(
                    modifier = Modifier
                        .padding(dimensions.small8dp)
                        .clickable {
                            agendaItem?.let { safeAgendaItem ->
                                onAction(
                                    AgendaDetailAction.OnDeleteAgendaItem(safeAgendaItem)
                                )
                            }
                        },
                    text = when (agendaItem) {
                        is AgendaItem.Task -> stringResource(R.string.Delete_task)
                        is AgendaItem.Reminder -> stringResource(R.string.Delete_reminder)
                        is AgendaItem.Event -> stringResource(R.string.Delete_event)
                        null -> stringResource(R.string.Delete_event)
                    },
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.W600),
                    color = colors.lightGray
                )
            }
        }
    }

}

@Composable
private fun VisitorsSection(
    visitors: List<Attendee>,
    onVisitorStatusChanged: () -> Unit,
    onShowDialog: () -> Unit,
    onDialogDismiss: () -> Unit,
    dialogState: DialogState,
    email: FieldInput = FieldInput(),
    emailErrorStatus: ErrorStatus,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    visitorFilter: VisitorFilter,
    isReadOnly: Boolean,
) {

    Column(modifier = Modifier.padding(top = dimensions.large32dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.visitors),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontSize = 20.sp
            )

            if (!isReadOnly) {
                Spacer(modifier = Modifier.width(dimensions.small8dp))
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(colors.light2),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.add_visitor),
                        tint = colors.lightGray,
                        modifier = Modifier.clickable {
                            onShowDialog()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.default16dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                VisitorFilter.ALL to stringResource(R.string.All),
                VisitorFilter.GOING to stringResource(R.string.Going),
                VisitorFilter.NOT_GOING to stringResource(R.string.Not_going)
            ).forEach { (filter, label) ->
                val isSelected = visitorFilter == filter

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onVisitorStatusChanged()
                            onUpdateState(AgendaDetailStateUpdate.UpdateVisitorFilter(filter))
                        }
                    },
                    label = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = typography.bodySmall.copy(
                                    fontWeight = FontWeight.W500,
                                    lineHeight = 15.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = if (isSelected) colors.white else colors.black,
                            )
                        }
                    },
                    modifier = Modifier
                        .requiredWidth(110.dp)
                        .requiredHeight(30.dp),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(1.dp, colors.transparent),
                    colors = FilterChipDefaults.filterChipColors()
                        .copy(
                            selectedContainerColor = colors.black,
                            containerColor = colors.light2
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.default16dp))

        if (visitorFilter == VisitorFilter.GOING || visitorFilter == VisitorFilter.ALL) {
            Text(
                text = stringResource(R.string.Going),
                style = typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500,
                    lineHeight = 15.sp
                ),
                textAlign = TextAlign.Start,
                color = colors.black,
            )
            Spacer(modifier = Modifier.height(dimensions.default16dp))

            visitors.forEach { visitor ->
                VisitorItem(
                    visitor = visitor,
                    onDeleteAttendee = { onAction(AgendaDetailAction.OnDeleteAttendee(it)) }
                )
                Spacer(modifier = Modifier.height(dimensions.small8dp))

            }
        }

        if (visitorFilter == VisitorFilter.NOT_GOING || visitorFilter == VisitorFilter.ALL) {
            Spacer(modifier = Modifier.height(dimensions.small8dp))

            Text(
                text = stringResource(R.string.Not_going),
                style = typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500,
                    lineHeight = 15.sp
                ),
                textAlign = TextAlign.Start,
                color = colors.black,
            )

            emptyList<Attendee>().forEach { visitor ->
                VisitorItem(
                    visitor = visitor,
                    onDeleteAttendee = { onAction(AgendaDetailAction.OnDeleteAttendee(visitor)) }
                )
                Spacer(modifier = Modifier.height(dimensions.small8dp))

            }
        }

        if (dialogState is DialogState.Show) {
            AddVisitorDialog(
                title = stringResource(R.string.Add_visitor),
                displayCloseIcon = true,
                positiveButtonText = stringResource(R.string.Add),
                onPositiveClick = { onAction(AgendaDetailAction.OnAddVisitorPressed) },
                onCancelClicked = { onDialogDismiss() },
                email = email,
                emailErrorStatus = emailErrorStatus,
                onUpdateState = onUpdateState,
            )
        }
    }
}

@Composable
private fun VisitorItem(
    visitor: Attendee,
    onDeleteAttendee: (Attendee) -> Unit
) {
    val visitorInitials by remember { mutableStateOf(getInitials(visitor.name)) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .height(46.dp)
                .background(colors.light2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .padding(start = dimensions.small8dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(colors.gray),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = visitorInitials,
                        style = typography.bodySmall.copy(color = colors.white)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.small8dp))

                Text(text = visitor.name, style = typography.bodySmall.copy(color = colors.black))
            }
            if (visitor.isCreator) {
                Text(
                    text = stringResource(R.string.creator),
                    style = typography.bodyMedium.copy(
                        color = colors.lightBlue,
                        fontWeight = FontWeight.W500
                    ),
                    modifier = Modifier.padding(end = dimensions.small8dp)
                )
            } else {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete icon",
                    modifier = Modifier
                        .padding(end = dimensions.small8dp)
                        .clickable { onDeleteAttendee(visitor) }
                )
            }
        }
    }

}

@Composable
private fun Header(
    onSavePressed: () -> Unit,
    onClosePressed: () -> Unit,
    onEnableEditPressed: () -> Unit,
    agendaItemId: String?,
    isReadOnly: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(colors.black),
        contentAlignment = Alignment.Center
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.default16dp)
                .padding(bottom = dimensions.extraLarge64dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onClosePressed() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = colors.white
                )
            }

            Text(
                text = "03 March 2024",
                style = typography.bodyLarge.copy(
                    fontWeight = FontWeight.W600,
                    lineHeight = 12.sp
                ),
                textAlign = TextAlign.Center,
                color = colors.white,
            )
            if (agendaItemId.isNullOrEmpty() || !isReadOnly) {
                Text(
                    modifier = Modifier.clickable { onSavePressed() },
                    text = stringResource(R.string.Save), color = colors.white
                )
            } else {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { onEnableEditPressed() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = colors.white
                    )
                }
            }
        }
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaDetailReadOnlyPreview() {
    AppTheme {
        AgendaDetailContent(
            state = AgendaDetailState(),
            uiState = AgendaDetailViewModel.AgendaDetailUiState.None,
            onAction = {},
            onUpdateState = {},
            agendaItemId = "12345",
            agendaItem = AgendaItem.Event(
                eventId = "12345",
                eventTitle = "ridens",
                eventDescription = null,
                from = 1221,
                to = 4855,
                photos = listOf(),
                attendees = listOf(),
                isUserEventCreator = false,
                host = null,
                remindAtTime = 4626,
            ),
            onUpdatePhotos = {},
            onShowDialog = {},
            onDialogDismiss = {},
            dialogState = DialogState.Hide,
            errorDialogState = AgendaDetailViewModel.ErrorDialogState.None,
            onErrorDialogDismiss = {}
        )
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaDetailEditablePreview() {
    AppTheme {
        AgendaDetailContent(
            state = AgendaDetailState(isReadOnly = false),
            uiState = AgendaDetailViewModel.AgendaDetailUiState.None,
            onAction = {},
            onUpdateState = {},
            agendaItemId = "12345",
            agendaItem = AgendaItem.Event(
                eventId = "12345",
                eventTitle = "ridens",
                eventDescription = null,
                from = 1221,
                to = 4855,
                photos = listOf(),
                attendees = listOf(),
                isUserEventCreator = false,
                host = null,
                remindAtTime = 4626,
            ),
            onUpdatePhotos = {},
            onShowDialog = {},
            onDialogDismiss = {},
            dialogState = DialogState.Hide,
            errorDialogState = AgendaDetailViewModel.ErrorDialogState.None,
            onErrorDialogDismiss = {}
        )
    }
}