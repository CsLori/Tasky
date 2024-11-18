package com.example.tasky.agenda.agenda_presentation.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_presentation.components.AgendaDetailOption
import com.example.tasky.agenda.agenda_presentation.components.DatePickerModal
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaUpdateState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaState
import com.example.tasky.core.domain.Result
import com.example.tasky.core.presentation.DateUtils
import com.example.tasky.core.presentation.DateUtils.getDaysWithDates
import com.example.tasky.core.presentation.DateUtils.localDateToStringddMMMMyyyyFormat
import com.example.tasky.core.presentation.DateUtils.toLocalDate
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.core.presentation.DateUtils.toMMMdHHmmFormat
import com.example.tasky.core.presentation.components.AgendaDetailDropdown
import com.example.tasky.core.presentation.components.AgendaDropdown
import com.example.tasky.core.presentation.components.LogoutDropdown
import com.example.tasky.core.presentation.components.TaskyLoader
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId

const val NUMBER_OF_DAYS_TO_SHOW = 5

@Composable
internal fun AgendaScreen(
    agendaViewModel: AgendaViewModel,
    onEditPressed: (AgendaItem) -> Unit,
    onLogoutNavigateToLogin: () -> Unit,
    onFabItemPressed: () -> Unit,
    onOpenPressed: (AgendaItem) -> Unit
) {
    val state by agendaViewModel.state.collectAsStateWithLifecycle()
    val uiState by agendaViewModel.uiState.collectAsStateWithLifecycle()
    val agendaItems by agendaViewModel.agendaItems.collectAsStateWithLifecycle()
    val syncResult by agendaViewModel.syncResult.collectAsStateWithLifecycle()
    val numberOfSyncItems by agendaViewModel.numberOfSyncItems.collectAsStateWithLifecycle()
    val userInitials = agendaViewModel.userInitials

    val context = LocalContext.current

    LaunchedEffect(syncResult) {
        if (numberOfSyncItems > 0) {
            when (syncResult) {
                is Result.Success -> {}
                is Result.Error -> {
                    //                showToast(context, (R.string.Failed_to_sync_agenda))
                }

                else -> { /* Do nothing */
                }
            }
        }
    }

    AgendaContent(
        state = state,
        agendaItems = agendaItems,
        userInitials = userInitials,
        uiState = uiState,
        onEditPressed = { action -> onEditPressed(action) },
        onUpdateState = { action -> agendaViewModel.updateState(action) },
        onAction = { action ->
            when (action) {
                is AgendaAction.OnDeleteAgendaItem -> agendaViewModel.deleteAgendaItem(action.agendaItem)
                AgendaAction.OnLogout -> {
                    agendaViewModel.logout()
                    onLogoutNavigateToLogin()
                }

                is AgendaAction.OnFabItemPressed -> {
                    onFabItemPressed()
                }

                is AgendaAction.OnOpenPressed -> {
                    onOpenPressed(action.agendaItem)
                }

                is AgendaAction.OnFilterAgendaItems -> agendaViewModel.getAgendaItems(action.filterDate.toLocalDate())
            }
        },
        onComplete = {
            agendaViewModel.updateState(AgendaUpdateState.UpdateIsDone(it))
        }
    )
}

@Composable
private fun AgendaContent(
    state: AgendaState,
    agendaItems: List<AgendaItem>,
    userInitials: String,
    uiState: AgendaViewModel.AgendaUiState,
    onEditPressed: (AgendaItem) -> Unit,
    onUpdateState: (AgendaUpdateState) -> Unit,
    onAction: (AgendaAction) -> Unit,
    onComplete: (Boolean) -> Unit
) {
    when (uiState) {
        AgendaViewModel.AgendaUiState.None -> {
            Scaffold(floatingActionButton = {
                FloatingActionButton(
                    containerColor = colors.black,
                    onClick = {
                        onUpdateState(AgendaUpdateState.UpdateVisibility(!state.isVisible))
                    },
                    shape = RoundedCornerShape(dimensions.default16dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add button",
                        tint = colors.white,
                    )
                    AgendaDropdown(
                        listItems = AgendaOption.entries,
                        onItemSelected = { agendaOption ->
                            onUpdateState(AgendaUpdateState.UpdateSelectedOption(agendaOption))
                            onAction(AgendaAction.OnFabItemPressed)
                        },
                        visible = state.isVisible
                    )
                }
            }, floatingActionButtonPosition = FabPosition.End) { innerPadding ->
                val cornerRadius = 30.dp
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.black),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensions.default16dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        onUpdateState(
                                            AgendaUpdateState.UpdateShouldShowDatePicker(
                                                !state.shouldShowDatePicker
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.month,
                                    style = typography.title,
                                    textAlign = TextAlign.Center,
                                    color = colors.white,
                                    modifier = Modifier.clickable {
                                        onUpdateState(
                                            AgendaUpdateState.UpdateShouldShowDatePicker(
                                                !state.shouldShowDatePicker
                                            )
                                        )
                                    }
                                )

                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "",
                                    tint = colors.white
                                )
                            }

                            if (state.shouldShowDatePicker) {
                                DatePickerModal(
                                    onDateSelected = { date ->
                                        date?.let { safeDate ->
                                            val result =
                                                DateUtils.convertMillisToLocalDate(safeDate)
                                            onUpdateState(
                                                AgendaUpdateState.UpdateMonth(
                                                    result.month.name
                                                )
                                            )

                                            onUpdateState(
                                                AgendaUpdateState.UpdateSelectedDate(
                                                    DateUtils.longToLocalDate(safeDate)
                                                )
                                            )

                                            onAction(AgendaAction.OnFilterAgendaItems(safeDate))
                                            onUpdateState(
                                                AgendaUpdateState.UpdateIsDateSelectedFromDatePicker(
                                                    true
                                                )
                                            )
                                        }
                                    },
                                    onDismiss = {
                                        onUpdateState(
                                            AgendaUpdateState.UpdateShouldShowDatePicker(
                                                false
                                            )
                                        )
                                    },
                                    initialDate = state.selectedDate.atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                )
                            }
                            UserInitialsButton(
                                userInitials = userInitials,
                                state = state,
                                onUpdateState = onUpdateState,
                                onAction = onAction
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = cornerRadius
                    ),
                    color = colors.white,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 150.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp)
                            .padding(horizontal = dimensions.default16dp)
                    ) {

                        CalendarDays(
                            state.selectedDate,
                            state.selectedIndex,
                            state.isDateSelectedFromDatePicker,
                            onSelectedIndexChanged = { selectedIndex, date ->
                                onUpdateState(
                                    AgendaUpdateState.UpdateSelectedIndex(
                                        selectedIndex
                                    )
                                )
                                onAction(AgendaAction.OnFilterAgendaItems(date))
                            })

                        val rowSelectedDate =
                            state.selectedDate.plusDays(state.selectedIndex.toLong())
                        Text(
                            if (rowSelectedDate == LocalDate.now()) stringResource(R.string.Today) else rowSelectedDate.localDateToStringddMMMMyyyyFormat(),
                            style = typography.calendarTitle,
                            modifier = Modifier.padding(vertical = dimensions.default16dp)
                        )

                        if (state.isLoading) {
                            TaskyLoader()
                        } else {

                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(
                                    items = agendaItems,
                                    key = { it.id }) { agendaItem ->
                                    when (agendaItem) {
                                        is AgendaItem.Task -> {
                                            AgendaItem(
                                                agendaItem = agendaItem,
                                                backgroundColor = colors.green,
                                                onDelete = {
                                                    onAction(
                                                        AgendaAction.OnDeleteAgendaItem(
                                                            agendaItem
                                                        )
                                                    )
                                                },
                                                onEditPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.TASK
                                                        )
                                                    )
                                                    onEditPressed(agendaItem)
                                                },
                                                onOpenPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.TASK
                                                        )
                                                    )
                                                    onAction(
                                                        AgendaAction.OnOpenPressed(
                                                            agendaItem
                                                        )
                                                    )
                                                },
                                                onComplete = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateIsDone(
                                                            !agendaItem.isDone

                                                        )
                                                    )
                                                },
                                            )
                                        }

                                        is AgendaItem.Event -> {
                                            AgendaItem(
                                                agendaItem = agendaItem,
                                                backgroundColor = colors.lightGreen,
                                                onDelete = { agendaItemId ->
                                                    onAction(
                                                        AgendaAction.OnDeleteAgendaItem(
                                                            agendaItem
                                                        )
                                                    )
                                                },
                                                onEditPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.EVENT
                                                        )
                                                    )
                                                    onEditPressed(agendaItem)
                                                },
                                                onOpenPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.EVENT
                                                        )
                                                    )
                                                    onAction(AgendaAction.OnOpenPressed(agendaItem))
                                                },
                                                onComplete = {}
                                            )
                                        }

                                        is AgendaItem.Reminder -> {
                                            AgendaItem(
                                                agendaItem = agendaItem,
                                                backgroundColor = colors.light2,
                                                onDelete = {
                                                    onAction(
                                                        AgendaAction.OnDeleteAgendaItem(
                                                            agendaItem
                                                        )
                                                    )
                                                },
                                                onEditPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.REMINDER
                                                        )
                                                    )
                                                    onEditPressed(agendaItem)
                                                },
                                                onOpenPressed = {
                                                    onUpdateState(
                                                        AgendaUpdateState.UpdateSelectedOption(
                                                            AgendaOption.REMINDER
                                                        )
                                                    )
                                                    onAction(AgendaAction.OnOpenPressed(agendaItem))
                                                },
                                                onComplete = {}
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        AgendaViewModel.AgendaUiState.Success -> {}
    }
}

@Composable
fun AgendaItem(
    agendaItem: AgendaItem,
    backgroundColor: Color,
    onDelete: (String) -> Unit,
    onEditPressed: () -> Unit,
    onOpenPressed: () -> Unit,
    onComplete: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(dimensions.default16dp))
            .height(150.dp)
            .background(backgroundColor)
    ) {
        var visible by remember { mutableStateOf(false) }
        val textColor = if (agendaItem is AgendaItem.Task) colors.white else colors.black
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .padding(dimensions.default16dp),
            ) {
                Icon(
                    imageVector = when (agendaItem) {
                        is AgendaItem.Task -> if (agendaItem.isDone) Icons.Outlined.CheckCircle else Icons.Outlined.Circle
                        else -> Icons.Outlined.Circle
                    },
                    contentDescription = "Icon checked",
                    tint = textColor,
                    modifier = Modifier.clickable {
                        onComplete()
                    }
                )

                Spacer(Modifier.width(dimensions.small8dp))

                Column {
                    Text(
                        text = agendaItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typography.title.copy(
                            lineHeight = 16.sp,
                            fontSize = 20.sp,
                            color = textColor,
                        ), textDecoration = when (agendaItem) {
                            is AgendaItem.Task -> if (agendaItem.isDone) TextDecoration.LineThrough else TextDecoration.None
                            else -> TextDecoration.None
                        }
                    )

                    Spacer(modifier = Modifier.height(dimensions.default16dp))

                    Text(
                        text = when (agendaItem) {
                            is AgendaItem.Task -> agendaItem.taskDescription ?: ""
                            is AgendaItem.Event -> agendaItem.eventDescription ?: ""
                            is AgendaItem.Reminder -> agendaItem.reminderDescription ?: ""
                        },
                        style = TextStyle(color = textColor),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(dimensions.default16dp))

            Box {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz, contentDescription = "Icon more",
                    modifier = Modifier
                        .padding(
                            top = dimensions.default16dp,
                            end = dimensions.default16dp
                        )
                        .clickable {
                            visible = !visible
                        },
                    tint = textColor
                )
                if (visible) {
                    AgendaDetailDropdown(
                        options = AgendaDetailOption.entries,
                        onItemSelected = {
                            when (it.option) {
                                AgendaDetailOption.DELETE.option -> {
                                    onDelete(agendaItem.id)
                                }

                                AgendaDetailOption.EDIT.option -> {
                                    onEditPressed()
                                }

                                AgendaDetailOption.OPEN.option -> {
                                    onOpenPressed()
                                }
                            }
                        },
                        visible = visible,
                        onDismiss = { visible = false },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = dimensions.default16dp, end = dimensions.default16dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = when (agendaItem) {
                    is AgendaItem.Task -> agendaItem.sortDate.toMMMdHHmmFormat()
                    is AgendaItem.Event -> "${agendaItem.sortDate.toMMMdHHmmFormat()} - ${agendaItem.to.toMMMdHHmmFormat()}"
                    is AgendaItem.Reminder -> agendaItem.sortDate.toMMMdHHmmFormat()
                },
                style = TextStyle(color = textColor)
            )
        }
    }
    Spacer(modifier = Modifier.height(dimensions.default16dp))
}

@Composable
fun CalendarDays(
    date: LocalDate,
    selectedIndex: Int,
    isDateSelectedFromDatePicker: Boolean,
    onSelectedIndexChanged: (Int, Long) -> Unit,
) {
    val days = remember(date) { getDaysWithDates(date, NUMBER_OF_DAYS_TO_SHOW) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.default16dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, (dayLetter, dayNumber) ->
            if (dayLetter != null) {
                val isSelected =
                    if (isDateSelectedFromDatePicker) index == 0 else index == selectedIndex
                CalendarComponent(
                    dayLetter = dayLetter,
                    dayNumber = dayNumber.toString(),
                    isSelected = isSelected,
                    onClick = {
                        val clickedDate = date.plusDays(index.toLong())
                        onSelectedIndexChanged(index, clickedDate.toLong())
                    }
                )
            }
        }
    }
}

@Composable
private fun CalendarComponent(
    dayLetter: String,
    dayNumber: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val componentHeight = 61.dp
    val componentWidth = 40.dp
    Column(
        modifier = Modifier
            .height(componentHeight)
            .width(componentWidth)
            .clip(RoundedCornerShape(dimensions.large24dp))
            .background(if (isSelected) colors.orange else colors.white)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            dayLetter,
            style = typography.calendarDayText.copy(color = if (isSelected) colors.darkGray else colors.gray)
        )
        Text(text = dayNumber, style = typography.calendarDayNumber)
    }
}

@Composable
fun UserInitialsButton(
    userInitials: String,
    state: AgendaState,
    onUpdateState: (AgendaUpdateState) -> Unit,
    onAction: (AgendaAction) -> Unit
) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = colors.light,
        onClick = {
            onUpdateState(AgendaUpdateState.UpdateVisibility(!state.isVisible))
        }
    ) {
        Box(
            modifier = Modifier
                .background(colors.light),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitials,
                color = colors.lightBlue,
                style = typography.userButton
            )
        }
        if (state.isVisible) {
            LogoutDropdown(
                onItemSelected = { onAction(AgendaAction.OnLogout) },
                visible = state.isVisible,
                onDismiss = {
                    onUpdateState(AgendaUpdateState.UpdateVisibility(false))
                }
            )
        }
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaContentPreview() {
    AppTheme {
        AgendaContent(
            state = AgendaState(
                agendaItems = listOf(
                    AgendaItem.Task(
                        taskId = "12345",
                        taskTitle = "Titlevcvcvcvcvcvvcvcvcvcvcvcvcvcvcvcvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv",
                        taskDescription = "sapien",
                        time = 1701,
                        remindAtTime = 7947,
                        isDone = true,
                    ),
                    AgendaItem.Task(
                        taskId = "1234",
                        taskTitle = "facilisi",
                        taskDescription = "sapien",
                        time = 1701,
                        remindAtTime = 7947,
                        isDone = false,
                    ),
                    AgendaItem.Event(
                        eventId = "334",
                        eventTitle = "imperdiet",
                        eventDescription = null,
                        from = 4415,
                        to = 2349,
                        photos = listOf(),
                        attendees = listOf(),
                        isUserEventCreator = false,
                        host = null,
                        remindAtTime = 9459
                    ),
                    AgendaItem.Event(
                        eventId = "545",
                        eventTitle = "imperdiet",
                        eventDescription = null,
                        from = 4415,
                        to = 2349,
                        photos = listOf(),
                        attendees = listOf(),
                        isUserEventCreator = false,
                        host = null,
                        remindAtTime = 9459
                    )
                ),
            ),
            onEditPressed = {},
            onUpdateState = {},
            onAction = { },
            uiState = AgendaViewModel.AgendaUiState.None,
            agendaItems = listOf(
                AgendaItem.Task(
                    taskId = "12345",
                    taskTitle = "facilisi",
                    taskDescription = "sapien",
                    time = 1701,
                    remindAtTime = 7947,
                    isDone = true,
                ),
                AgendaItem.Task(
                    taskId = "1234",
                    taskTitle = "facilisi",
                    taskDescription = "sapien",
                    time = 1701,
                    remindAtTime = 7947,
                    isDone = false,
                ),
                AgendaItem.Event(
                    eventId = "334",
                    eventTitle = "imperdiet",
                    eventDescription = null,
                    from = 4415,
                    to = 2349,
                    photos = listOf(),
                    attendees = listOf(),
                    isUserEventCreator = false,
                    host = null,
                    remindAtTime = 9459
                ),
                AgendaItem.Event(
                    eventId = "545",
                    eventTitle = "imperdiet",
                    eventDescription = null,
                    from = 4415,
                    to = 2349,
                    photos = listOf(),
                    attendees = listOf(),
                    isUserEventCreator = false,
                    host = null,
                    remindAtTime = 9459
                )
            ),
            onComplete = {},
            userInitials = "Lorant Csuhai"
        )
    }
}