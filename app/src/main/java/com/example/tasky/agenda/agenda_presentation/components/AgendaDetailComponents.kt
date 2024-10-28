package com.example.tasky.agenda.agenda_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.RemindBeforeDuration
import com.example.tasky.core.presentation.DateUtils
import com.example.tasky.core.presentation.DateUtils.toHourMinuteFormat
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.core.presentation.components.ReminderDropdown
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AgendaItemMainHeader(
    displayName: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.padding(bottom = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            modifier = Modifier
                .size(24.dp),
            imageVector = Icons.Rounded.Square,
            tint = iconColor,
            contentDescription = "Icon checked"
        )

        Spacer(Modifier.width(dimensions.small8dp))

        Text(
            text = displayName,
            style = typography.bodyLarge.copy(lineHeight = 20.sp)
        )
    }
}

@Composable
fun AgendaItemTitle(
    isReadOnly: Boolean,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensions.default16dp)
            .then(if (isReadOnly) {
                Modifier
            } else {
                Modifier.clickable {
                    onUpdateState(AgendaDetailStateUpdate.UpdateEditType(EditType.TITLE))
                    onAction(AgendaDetailAction.OnEditRowPressed)
                }
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
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
        if (!isReadOnly) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Navigate next"
            )
        }
    }
    DefaultHorizontalDivider()
}

@Composable
fun AgendaItemDescription(
    isReadOnly: Boolean,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (isReadOnly) {
                    Modifier
                } else {
                    Modifier.clickable {
                        onUpdateState(AgendaDetailStateUpdate.UpdateEditType(EditType.DESCRIPTION))
                        onAction(AgendaDetailAction.OnEditRowPressed)
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        Text(
            text = state.task.description ?: "",
            style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
        )
        if (!isReadOnly) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Navigate next"
            )
        }
    }
    DefaultHorizontalDivider()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimeAndDateRow(
    isReadOnly: Boolean,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    state: AgendaDetailState
) {
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
                modifier = Modifier.then(
                    if (isReadOnly) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            onUpdateState(
                                AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                    !state.shouldShowTimePicker
                                )
                            )
                        }
                    }
                ),
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
                .then(
                    if (isReadOnly) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            onUpdateState(
                                AgendaDetailStateUpdate.UpdateShouldShowDatePicker(
                                    !state.shouldShowDatePicker
                                )
                            )
                        }
                    }
                ),
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
}

@Composable
fun AddPhotosSection(
    onAddPhotos: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.light2)
            .clickable(onClick = onAddPhotos)
            .padding(vertical = dimensions.large32dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add photos",
            tint = colors.gray
        )

        Spacer(modifier = Modifier.width(dimensions.default16dp))

        Text(
            "Add photos", style = typography.bodyLarge.copy(
                lineHeight = 18.sp, letterSpacing = 0.sp, color = colors.gray
            )
        )
    }
    DefaultHorizontalDivider()
}

@Composable
fun SetReminderRow(
    isReadOnly: Boolean,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    state: AgendaDetailState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (isReadOnly) {
                    Modifier
                } else {
                    Modifier.clickable {
                        onUpdateState(AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown(true))
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
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
                text = when ((state.selectedReminder.milliseconds)) {
                    RemindBeforeDuration.TEN_MINUTES.duration -> stringResource(R.string.ten_minutes_before)
                    RemindBeforeDuration.THIRTY_MINUTES.duration -> stringResource(R.string.thirty_minutes_before)
                    RemindBeforeDuration.ONE_HOUR.duration -> stringResource(R.string.one_hour_before)
                    RemindBeforeDuration.SIX_HOURS.duration -> stringResource(R.string.six_hours_before)
                    RemindBeforeDuration.ONE_DAY.duration -> stringResource(R.string.one_day_before)
                    else -> stringResource(R.string.thirty_minutes_before)
                },
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )
        }

        if (!isReadOnly) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Navigate next"
            )
        }
    }
    if (state.shouldShowReminderDropdown) {
        ReminderDropdown(
            options = reminderOptions,
            onItemSelected = {
                onUpdateState(AgendaDetailStateUpdate.UpdateSelectedReminder(it.timeBeforeInMillis))
                onUpdateState(AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown(false))
            },
            visible = state.shouldShowReminderDropdown,
            onDismiss = {
                onUpdateState(
                    AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown(
                        false
                    )
                )
            }
        )
    }
    DefaultHorizontalDivider()
}