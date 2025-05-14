@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.NotificationPermissionUtil
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.components.AddPhotosSection
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemDescription
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemMainHeader
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemTitle
import com.example.tasky.agenda.agenda_presentation.components.SetReminderRow
import com.example.tasky.agenda.agenda_presentation.components.TimeAndDateRow
import com.example.tasky.agenda.agenda_presentation.components.thirtyMinutesInMillis
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.state.AgendaDetailStateUpdate
import com.example.tasky.agenda.agenda_presentation.state.VisitorFilter
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.core.presentation.DateUtils.toStringMMMdyyyyFormat
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.components.AddVisitorDialog
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.presentation.components.ErrorDialog
import com.example.tasky.core.presentation.components.TaskyLoader
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.getInitials
import timber.log.Timber
import java.time.LocalDateTime

const val MAX_SESSION_COUNT = 2

@Composable
internal fun AgendaDetailScreen(
    agendaDetailViewModel: AgendaDetailViewModel,
    onNavigateToAgendaScreen: () -> Unit,
    onEditPressed: () -> Unit,
    agendaItemId: String? = null,
    onNavigateToSelectedPhoto: (String?) -> Unit
) {
    val state by agendaDetailViewModel.state.collectAsStateWithLifecycle()
    val uiState by agendaDetailViewModel.uiState.collectAsStateWithLifecycle()
    val dialogState by agendaDetailViewModel.dialogState.collectAsStateWithLifecycle()
    val errorDialogState by agendaDetailViewModel.errorDialogState.collectAsStateWithLifecycle()
    val sessionCount by agendaDetailViewModel.sessionCount.collectAsStateWithLifecycle()
    val hasSeenNotificationPrompt by agendaDetailViewModel.hasSeenNotificationPrompt.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissions = remember { NotificationPermissionUtil(context) }
    var areNotificationsEnabled by remember {
        mutableStateOf(
            permissions.arePushNotificationsEnabledOnTheDevice(
                context
            )
        )
    }

    LaunchedEffect(agendaItemId) {
        if (agendaItemId == null) {
            agendaDetailViewModel.updateState(
                AgendaDetailStateUpdate.UpdateSelectedAgendaItem(
                    when (agendaDetailViewModel.agendaOption) {
                        AgendaOption.TASK -> AgendaItem(
                            id = state.id,
                            title = "Task",
                            description = "New task",
                            time = state.time,
                            remindAt = state.remindAt,
                            details = AgendaItemDetails.Task(isDone = false)
                        )

                        AgendaOption.EVENT -> AgendaItem(
                            id = state.id,
                            title = "Event",
                            description = "New event",
                            time = state.time,
                            remindAt = state.remindAt,
                            details = AgendaItemDetails.Event(
                                toTime = ((state.details as? AgendaItemDetails.Event)?.toTime?.toLong()
                                    ?.plus(thirtyMinutesInMillis))?.toLocalDateTime()
                                    ?: (LocalDateTime.now()
                                        .toLong() + thirtyMinutesInMillis).toLocalDateTime(),
                                attendees = (state.details as? AgendaItemDetails.Event)?.attendees
                                    ?: emptyList(),
                                photos = (state.details as? AgendaItemDetails.Event)?.photos
                                    ?: emptyList(),
                                isUserEventCreator = true,
                                host = (state.details as? AgendaItemDetails.Event)?.host ?: ""
                            ),
                        )

                        AgendaOption.REMINDER -> AgendaItem(
                            id = state.id,
                            title = "Reminder",
                            description = "New reminder",
                            time = state.time,
                            remindAt = state.remindAt,
                            details = AgendaItemDetails.Reminder
                        )
                    }
                )
            )
        } else {
            agendaDetailViewModel.loadAgendaItem(agendaItemId)
        }
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isEnabled = permissions.arePushNotificationsEnabledOnTheDevice(context)
                if (isEnabled != areNotificationsEnabled) {
                    areNotificationsEnabled = isEnabled
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
                        agendaDetailViewModel.createAgendaItem()
                    } else {
                        agendaDetailViewModel.updateAgendaItem()
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
                    VisitorFilter.ALL -> (state.details as? AgendaItemDetails.Event)?.attendees
                    VisitorFilter.GOING -> (state.details as? AgendaItemDetails.Event)?.attendees?.filter { it.isGoing }
                    VisitorFilter.NOT_GOING -> (state.details as? AgendaItemDetails.Event)?.attendees?.filterNot { it.isGoing }
                }

                is AgendaDetailAction.OnDeleteAgendaItem -> {
                    agendaDetailViewModel.deleteAgendaItem(action.agendaItem)
                    onNavigateToAgendaScreen()
                }

                is AgendaDetailAction.OnDeleteAttendee -> {
                    agendaDetailViewModel.deleteAttendee(action.attendee)
                    onNavigateToAgendaScreen()
                }

                is AgendaDetailAction.OnNotificationPromptSeen -> {
                    agendaDetailViewModel.setHasSeenNotificationPrompt(action.hasSeenNotificationPrompt)
                }
            }
        },
        agendaItemId = agendaItemId,
        agendaItem = state.details?.let {
            AgendaItem(
                id = state.id,
                title = state.title,
                description = state.description,
                time = state.time,
                details = it,
                remindAt = state.remindAt
            )
        },
        onUpdatePhotos = { photos ->
            agendaDetailViewModel.updateState(AgendaDetailStateUpdate.UpdatePhotos(photos))
        },
        onShowDialog = { agendaDetailViewModel.showAddVisitorDialog() },
        onDialogDismiss = { agendaDetailViewModel.hideAddVisitorDialog() },
        onErrorDialogDismiss = { agendaDetailViewModel.hideErrorDialog() },
        dialogState = dialogState,
        errorDialogState = errorDialogState,
        areNotificationsEnabled = areNotificationsEnabled,
        hasSeenNotificationPrompt = hasSeenNotificationPrompt,
        sessionCount = sessionCount,
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
    onErrorDialogDismiss: () -> Unit,
    areNotificationsEnabled: Boolean,
    hasSeenNotificationPrompt: Boolean,
    sessionCount: Int,
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val getContent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }
    val context = LocalContext.current

    if (state.isLoading) {
        TaskyLoader()
    }
    when (uiState) {
        AgendaDetailViewModel.AgendaDetailUiState.Success -> {
            onAction(AgendaDetailAction.OnCreateSuccess)
        }

        AgendaDetailViewModel.AgendaDetailUiState.None -> {
            if (dialogState is DialogState.ShowError) {
                when (errorDialogState) {
                    is AgendaDetailViewModel.ErrorDialogState.AgendaItemError -> {
                        ErrorDialog(
                            label = errorDialogState.message.asString(),
                            displayCloseIcon = false,
                            positiveButtonText = stringResource(R.string.OK),
                            positiveOnClick = { onErrorDialogDismiss() },
                        )
                    }

                    is AgendaDetailViewModel.ErrorDialogState.AttendeeError -> {
                        ErrorDialog(
                            label = errorDialogState.message.asString(),
                            displayCloseIcon = false,
                            positiveButtonText = stringResource(R.string.OK),
                            positiveOnClick = { onErrorDialogDismiss() },
                        )
                    }

                    is AgendaDetailViewModel.ErrorDialogState.GeneralError -> {
                        ErrorDialog(
                            label = errorDialogState.message.asString(),
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
                    date = state.time.toStringMMMdyyyyFormat(),
                    onAction = onAction,
                    agendaItemId = agendaItemId,
                    isReadOnly = state.isReadOnly,
                    sessionCount = sessionCount,
                    hasSeenNotificationPrompt = hasSeenNotificationPrompt,
                    areNotificationsEnabled = areNotificationsEnabled,
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

    val isUserEventCreator =
        (state.details as? AgendaItemDetails.Event)?.isUserEventCreator ?: false
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.default16dp)
    ) {
        AgendaItemMainHeader(agendaItem)

        AgendaItemTitle(onUpdateState, onAction, state, isUserEventCreator)

        AgendaItemDescription(onUpdateState, onAction, state, isUserEventCreator)

        if (agendaItem?.details is AgendaItemDetails.Event) {
            TimeAndDateRow(
                agendaItem = agendaItem,
                text = stringResource(R.string.from),
                onUpdateState = onUpdateState,
                state = state,
                isEventCreator = isUserEventCreator
            )

            DefaultHorizontalDivider()

            TimeAndDateRow(
                agendaItem = agendaItem,
                text = stringResource(R.string.to),
                onUpdateState = onUpdateState,
                state = state,
                endTime = true,
                isEventCreator = isUserEventCreator
            )
        } else {
            if (agendaItem != null) {
                TimeAndDateRow(
                    agendaItem = agendaItem,
                    text = stringResource(R.string.at),
                    onUpdateState = onUpdateState,
                    state = state,
                    isEventCreator = isUserEventCreator
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (agendaItem?.details is AgendaItemDetails.Event) {
            AddPhotosSection(
                isReadOnly = state.isReadOnly,
                onAddPhotos = onAddPhotosPressed,
                selectedImageUri = selectedImageUri,
                onUpdateState = onUpdateState,
                photos = (state.details as? AgendaItemDetails.Event)?.photos ?: emptyList(),
                onAction = onAction,
                isEventCreator = isUserEventCreator
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

        if (agendaItem?.details is AgendaItemDetails.Event) {
            VisitorsSection(
                visitors = (state.details as? AgendaItemDetails.Event)?.attendees ?: emptyList(),
                onVisitorStatusChanged = { onAction(AgendaDetailAction.OnVisitorFilterChanged) },
                onShowDialog = onShowDialog,
                onDialogDismiss = onDialogDismiss,
                dialogState = dialogState,
                email = state.addVisitorEmail ?: FieldInput(""),
                emailErrorStatus = state.emailErrorStatus ?: ErrorStatus(false),
                onUpdateState = onUpdateState,
                onAction = onAction,
                visitorFilter = state.visitorFilter,
                isReadOnly = state.isReadOnly,
                isEventCreator = isUserEventCreator
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
                if (agendaItem?.details !is AgendaItemDetails.Event) DefaultHorizontalDivider()

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
                    text = when (agendaItem?.details) {
                        is AgendaItemDetails.Task -> stringResource(R.string.Delete_task)
                        is AgendaItemDetails.Reminder -> stringResource(R.string.Delete_reminder)
                        is AgendaItemDetails.Event -> if ((agendaItem?.details as? AgendaItemDetails.Event)?.isUserEventCreator == true) stringResource(
                            R.string.Delete_event
                        ) else stringResource(
                            R.string.Leave_event
                        )

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
    isEventCreator: Boolean
) {
    Timber.d("DDD - Visitors: $visitors")
    Column(modifier = Modifier.padding(top = dimensions.large32dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.visitors),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontSize = 20.sp
            )

            if (!isReadOnly && isEventCreator) {
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

            visitors.filter { visitor -> visitor.isGoing }.forEach { visitor ->
                VisitorItem(
                    visitor = visitor,
                    onDeleteAttendee = { onAction(AgendaDetailAction.OnDeleteAttendee(it)) }
                )
                Spacer(modifier = Modifier.height(dimensions.small8dp))

            }
        }

        if (visitorFilter == VisitorFilter.NOT_GOING || visitorFilter == VisitorFilter.ALL && visitors.any { visitor -> !visitor.isGoing }) {
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

            visitors.filter { visitor -> !visitor.isGoing }.forEach { visitor ->
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
    onDeleteAttendee: (Attendee) -> Unit,
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
    date: String,
    onAction: (AgendaDetailAction) -> Unit,
//    onSavePressed: () -> Unit,
//    onClosePressed: () -> Unit,
//    onEnableEditPressed: () -> Unit,
    agendaItemId: String?,
    isReadOnly: Boolean,
    areNotificationsEnabled: Boolean,
    hasSeenNotificationPrompt: Boolean,
    sessionCount: Int,
//    onNotificationPromptSeen: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var shouldShowNotificationPrompt by remember { mutableStateOf(false) }

    if (shouldShowNotificationPrompt) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.Enable_notifications)) },
            text = { Text(stringResource(R.string.Notifications_disabled_open_settings)) },
            confirmButton = {
                TextButton(onClick = {
                    onAction(AgendaDetailAction.OnNotificationPromptSeen(true))
                    shouldShowNotificationPrompt = false
                    val intent =
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(
                                Settings.EXTRA_APP_PACKAGE,
                                context.packageName
                            )
                        }
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.Go_to_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onAction(AgendaDetailAction.OnNotificationPromptSeen(true))
                    shouldShowNotificationPrompt = false
                }) {
                    Text(stringResource(R.string.Maybe_later))
                }
            }
        )
    }

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
                .padding(bottom = dimensions.large32dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onAction(AgendaDetailAction.OnClosePressed) },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = colors.white
                )
            }

            Text(
                text = date,
                style = typography.bodyLarge.copy(
                    fontWeight = FontWeight.W600,
                    lineHeight = 12.sp
                ),
                textAlign = TextAlign.Center,
                color = colors.white,
            )
            if (agendaItemId.isNullOrEmpty() || !isReadOnly) {
                Text(
                    modifier = Modifier.clickable {
                        if (!areNotificationsEnabled && !hasSeenNotificationPrompt && sessionCount < MAX_SESSION_COUNT) {
                            shouldShowNotificationPrompt = true
                        } else {
                            shouldShowNotificationPrompt = false
                            onAction(AgendaDetailAction.OnSavePressed)
                        }
                    },
                    text = stringResource(R.string.Save), color = colors.white
                )
            } else {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { onAction(AgendaDetailAction.OnEnableEditPressed) },
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
            state = AgendaDetailState(isReadOnly = true),
            uiState = AgendaDetailViewModel.AgendaDetailUiState.None,
            onUpdateState = {},
            onAction = {},
            agendaItemId = "12345",
            agendaItem = AgendaItem(
                id = "33232233234",
                title = "imperdisdsdsdsdet",
                description = "sdsssdsd",
                time = LocalDateTime.now(),
                details = AgendaItemDetails.Event(
                    toTime = LocalDateTime.now(),
                    photos = listOf(),
                    attendees = listOf(
                        Attendee(
                            email = "cecelia.cummings@example.com",
                            name = "Benito Conway",
                            userId = "laudem",
                            eventId = "decore",
                            isGoing = false,
                            remindAt = 5235,
                            isCreator = false
                        )
                    ),
                    isUserEventCreator = false,
                    host = "123213123",
                ),
                remindAt = LocalDateTime.now()
            ),
            onUpdatePhotos = {},
            onShowDialog = {},
            onDialogDismiss = {},
            dialogState = DialogState.Hide,
            errorDialogState = AgendaDetailViewModel.ErrorDialogState.None,
            onErrorDialogDismiss = {},
            areNotificationsEnabled = true,
            hasSeenNotificationPrompt = true,
            sessionCount = 2,
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
            onUpdateState = {},
            onAction = {},
            agendaItemId = "12345",
            agendaItem = AgendaItem(
                id = "33232233234",
                title = "imperdisdsdsdsdet",
                description = "sdsssdsd",
                time = LocalDateTime.now(),
                details = AgendaItemDetails.Event(
                    toTime = LocalDateTime.now(),
                    photos = listOf(),
                    attendees = listOf(
                        Attendee(
                            email = "cecelia.cummings@example.com",
                            name = "Benito Conway",
                            userId = "laudem",
                            eventId = "decore",
                            isGoing = false,
                            remindAt = 5235,
                            isCreator = false
                        )
                    ),
                    isUserEventCreator = false,
                    host = "12312123",
                ),
                remindAt = LocalDateTime.now()
            ),
            onUpdatePhotos = {},
            onShowDialog = {},
            onDialogDismiss = {},
            dialogState = DialogState.Hide,
            errorDialogState = AgendaDetailViewModel.ErrorDialogState.None,
            onErrorDialogDismiss = {},
            areNotificationsEnabled = true,
            hasSeenNotificationPrompt = true,
            sessionCount = 2,
        )
    }
}