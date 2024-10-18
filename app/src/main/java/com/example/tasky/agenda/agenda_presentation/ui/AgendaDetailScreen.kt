@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.components.DatePickerModal
import com.example.tasky.agenda.agenda_presentation.components.TimePickerDialog
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.DateUtils
import com.example.tasky.util.DateUtils.toHourMinuteFormat
import java.time.ZoneOffset


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
internal fun AgendaDetailScreen(
    agendaDetailViewModel: AgendaDetailViewModel,
    onNavigateToAgendaScreen: () -> Unit,
    onClose: () -> Boolean
) {
    val state = agendaDetailViewModel.state.collectAsState().value
    val uiState = agendaDetailViewModel.uiState.collectAsState().value
    AgendaDetailContent(
        state = state,
        uiState = uiState,
        onUpdateState = { action  -> agendaDetailViewModel.updateState(action) },
        onAction = { action ->
            when (action) {
                AgendaDetailAction.OnClosePress -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnCreateSuccess -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnEditField -> {}
                AgendaDetailAction.OnReminderPress -> {}
                AgendaDetailAction.OnSavePress -> agendaDetailViewModel.createTask()
            }
        })
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
private fun AgendaDetailContent(
    state: AgendaDetailState,
    uiState: AgendaDetailViewModel.AgendaDetailUiState,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit
) {
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
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Header(
                        onSavePressed = { onAction(AgendaDetailAction.OnSavePress) },
                        onClosePressed = { onAction(AgendaDetailAction.OnClosePress) })
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = dimensions.cornerRadius30dp,
                        topEnd = dimensions.cornerRadius30dp
                    ),
                    color = colors.white,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 70.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = 40.dp)
                            .padding(horizontal = dimensions.default16dp)
                    ) {
                        MainContent(
                            state = state,
                            onUpdateState = onUpdateState,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainContent(
    state: AgendaDetailState,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit
) {
    Row(
        modifier = Modifier.padding(bottom = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            modifier = Modifier
                .size(24.dp),
            imageVector = Icons.Rounded.Square,
            tint = colors.green,
            contentDescription = "Icon checked"
        )

        Spacer(Modifier.width(dimensions.small8dp))

        Text(
            text = AgendaOption.TASK.displayName,
            style = typography.bodyLarge.copy(lineHeight = 20.sp)
        )
    }



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = "Icon checked"
            )

            Spacer(Modifier.width(dimensions.small8dp))

            Text(
                text = state.task.title,
                style = typography.title.copy(lineHeight = 25.sp, fontSize = 26.sp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
            contentDescription = "Navigate next"
        )
    }
    DefaultHorizontalDivider()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = state.task.description,
            style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
            contentDescription = "Navigate next"
        )
    }
    DefaultHorizontalDivider()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "At",
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )
            Spacer(Modifier.width(dimensions.large32dp))

            Text(
                modifier = Modifier.clickable {
                    onUpdateState(
                        AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                            !state.shouldShowTimePicker
                        )
                    )
                },
                text = state.task.time.toHourMinuteFormat(),
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )

            if (state.shouldShowTimePicker)
                TimePickerDialog(
                    onDismissRequest = {
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                false
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedHour = state.time.hour
                                val selectedMinute = state.time.minute

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                        shouldShowTimePicker = false
                                    )
                                )
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateTime(
                                        hour = selectedHour,
                                        minute = selectedMinute
                                    )
                                )
                            }
                        ) { Text(stringResource(R.string.OK)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                        false
                                    )
                                )
                            }
                        ) { Text(stringResource(R.string.cancel)) }
                    },
                    content = {
                        TimePicker(
                            state = state.time
                        )

                    }
                )
        }

        Text(
            modifier = Modifier
                .padding(end = dimensions.extraLarge64dp)
                .clickable {
                    onUpdateState(
                        AgendaDetailStateUpdate.UpdateShouldShowDatePicker(
                            !state.shouldShowDatePicker
                        )
                    )
                },
            text = state.date,
            style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
        )

        if (state.shouldShowDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    date?.let { safeDate ->
                        val result = DateUtils.convertMillisToLocalDate(safeDate)
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateMonth(
                                result.month.name
                            )
                        )

                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateDate(
                                DateUtils.longToLocalDate(safeDate)
                            )
                        )
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateShouldShowDatePicker(
                                true
                            )
                        )
                    }
                },
                onDismiss = {
                    onUpdateState(
                        AgendaDetailStateUpdate.UpdateShouldShowDatePicker(
                            false
                        )
                    )
                },
                initialDate = state.selectedDate.atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()
            )
        }
    }

    DefaultHorizontalDivider()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .clickable {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(30.dp), contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Rounded.Square,
                    tint = colors.light2,
                    contentDescription = "Rounded Square"
                )
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    tint = colors.gray,
                    contentDescription = "Notifications Icon"
                )

            }

            Spacer(Modifier.width(dimensions.small8dp))

            Text(
                text = "30 minutes before",
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
            contentDescription = "Navigate next"
        )
    }
    DefaultHorizontalDivider()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimensions.large32dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultHorizontalDivider()
            Spacer(modifier = Modifier.height(dimensions.small8dp))
            Text(
                modifier = Modifier
                    .padding(dimensions.small8dp)
                    .clickable { },
                text = stringResource(R.string.delete_task),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.W600),
                color = colors.lightGray
            )
        }
    }
}


@Composable
fun EventContent() {
    Text(AgendaOption.EVENT.displayName)
}

@Composable
fun ReminderContent() {
    Text(AgendaOption.REMINDER.displayName)
}

@Composable
private fun Header(
    onSavePressed: () -> Unit,
    onClosePressed: () -> Unit
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

            if (true) { // This will need changing
                Text(modifier = Modifier.clickable {
                    onSavePressed()
                }, text = "Save", color = colors.white)
            } else {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { },
                ) {
                    Icon(
                        modifier = Modifier.clickable {
                            onSavePressed()
                        },
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = colors.white
                    )
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaDetailPreview() {
    AppTheme {
        AgendaDetailContent(
            state = AgendaDetailState(),
            uiState = AgendaDetailViewModel.AgendaDetailUiState.None,
            onAction = {},
            onUpdateState = {}
        )
    }
}
