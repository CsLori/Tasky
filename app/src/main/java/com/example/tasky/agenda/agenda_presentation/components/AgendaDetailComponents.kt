@file:Suppress("OPT_IN_USAGE_FUTURE_ERROR", "OPT_IN_USAGE_FUTURE_ERROR")

package com.example.tasky.agenda.agenda_presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.RemindBeforeDuration
import com.example.tasky.core.presentation.DateUtils.toHourMinuteFormat
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.core.presentation.DateUtils.toStringMMMdyyyyFormat
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.core.presentation.components.ReminderDropdown
import com.example.tasky.core.presentation.components.showToast
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import timber.log.Timber
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

const val thirtyMinutesInMillis = 1800000L // 30 * 60 * 1000
const val MAX_NUMBER_OF_PHOTOS = 9

@Composable
fun AgendaItemMainHeader(agendaItem: AgendaItem?) {
    Row(
        modifier = Modifier.padding(bottom = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            modifier = Modifier
                .size(24.dp),
            imageVector = Icons.Rounded.Square,
            tint = when (agendaItem?.details) {
                is AgendaItemDetails.Task -> colors.green
                is AgendaItemDetails.Event -> colors.lightGreen
                is AgendaItemDetails.Reminder -> colors.gray
                null -> colors.green
            },
            contentDescription = "Icon checked"
        )

        Spacer(Modifier.width(dimensions.small8dp))

        Text(
            text = when (agendaItem?.details) {
                is AgendaItemDetails.Task -> AgendaOption.TASK.displayName
                is AgendaItemDetails.Event -> AgendaOption.EVENT.displayName
                is AgendaItemDetails.Reminder -> AgendaOption.REMINDER.displayName
                null -> AgendaOption.TASK.displayName
            },
            style = typography.bodyLarge.copy(lineHeight = 20.sp)
        )
    }
}

@Composable
fun AgendaItemTitle(
    agendaItem: AgendaItem?,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensions.default16dp)
            .then(if (!state.isReadOnly) {
                Modifier.clickable {
                    onUpdateState(AgendaDetailStateUpdate.UpdateEditType(EditType.TITLE))
                    onAction(AgendaDetailAction.OnEditRowPressed)
                }
            } else {
                Modifier
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (state.isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = "Icon checked"
            )

            Spacer(Modifier.width(dimensions.small8dp))

            Text(
                text = state.title,
                style = typography.title.copy(lineHeight = 25.sp, fontSize = 26.sp)
            )
        }
        if (!state.isReadOnly) {
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
    agendaItem: AgendaItem?,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (!state.isReadOnly) {
                    Modifier.clickable {
                        onUpdateState(AgendaDetailStateUpdate.UpdateEditType(EditType.DESCRIPTION))
                        onAction(AgendaDetailAction.OnEditRowPressed)
                    }
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (state.isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        Text(
            text = state.description,
            style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
        )
        if (!state.isReadOnly) {
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
    agendaItem: AgendaItem,
    text: String,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    state: AgendaDetailState,
    isSecondRow: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Row(
            modifier = Modifier
                .width(120.dp)
                .weight(1f)
                .then(
                    if (!state.isReadOnly) {
                        Modifier.clickable {
                            if (isSecondRow) {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowSecondRowTimePicker(
                                        !state.shouldShowSecondRowTimePicker
                                    )
                                )
                            } else {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                        !state.shouldShowTimePicker
                                    )
                                )
                            }
                        }
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(0.4f),
                text = text,
                style = typography.bodyLarge.copy(
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.W400
                )
            )

            Row(
                modifier = Modifier.weight(0.6f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (agendaItem.details) {
                        is AgendaItemDetails.Task -> state.selectedDate?.toHourMinuteFormat() ?: ""
                        is AgendaItemDetails.Event -> if (isSecondRow) (state.details as? AgendaItemDetails.Event)?.toTime?.toHourMinuteFormat()
                            ?: LocalDateTime.now()
                                .toHourMinuteFormat() else state.time.toHourMinuteFormat()

                        is AgendaItemDetails.Reminder -> state.time.toHourMinuteFormat()
                    },

                    style = typography.bodyLarge.copy(
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.W400
                    )
                )

                if (!state.isReadOnly) {
                    Icon(
                        modifier = Modifier.padding(end = dimensions.default16dp),
                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                        contentDescription = "Navigate next"
                    )
                }
            }

            if (state.shouldShowTimePicker && !isSecondRow) {
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
                                val selectedHour = state.firstRowTimePickerState.hour
                                val selectedMinute = state.firstRowTimePickerState.minute

                                val updatedTime = state.time.withHour(selectedHour)
                                    ?.withMinute(selectedMinute)

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateTime(
                                        updatedTime ?: LocalDateTime.now()
                                    )
                                )
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowTimePicker(
                                        shouldShowTimePicker = false
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
                        ) { Text(stringResource(R.string.Cancel)) }
                    },
                    content = {
                        TimePicker(state = state.firstRowTimePickerState)
                    }
                )
            }

            if (state.shouldShowSecondRowTimePicker) {
                TimePickerDialogSecondRow(
                    onDismissRequest = {
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateShouldShowSecondRowTimePicker(
                                false
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedHour = state.secondRowTimePickerState.hour
                                val selectedMinute = state.secondRowTimePickerState.minute

                                val updatedTime =
                                    (state.details as? AgendaItemDetails.Event)?.toTime
                                        ?.withHour(selectedHour)
                                        ?.withMinute(selectedMinute)

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateEventSecondRowTime(
                                        updatedTime ?: LocalDateTime.now()
                                    )
                                )

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowSecondRowTimePicker(
                                        shouldShowTimePicker = false
                                    )
                                )
                            }
                        ) { Text(stringResource(R.string.OK)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowSecondRowTimePicker(
                                        false
                                    )
                                )
                            }
                        ) { Text(stringResource(R.string.Cancel)) }
                    },
                    content = {
                        TimePicker(state = state.secondRowTimePickerState)

                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (!state.isReadOnly) {
                        Modifier.clickable {
                            if (isSecondRow) {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowSecondRowDatePicker(
                                        !state.shouldShowSecondRowDatePicker
                                    )
                                )
                            } else {
                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateShouldShowDatePicker(
                                        !state.shouldShowDatePicker
                                    )
                                )
                            }
                        }
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = dimensions.default16dp),
                text = if (isSecondRow) (state.secondRowDate?.toStringMMMdyyyyFormat()
                    ?: LocalDateTime.now()
                        .toStringMMMdyyyyFormat()) else state.time.toStringMMMdyyyyFormat(),
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )
            if (!state.isReadOnly) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Navigate next"
                )
            }
        }

        if (state.shouldShowDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    date?.let { safeDate ->
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateTime(
                                safeDate.toLocalDateTime()
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
                initialDate = state.time.toLong()
            )
        }

        if (state.shouldShowSecondRowDatePicker) {
            DatePickerModalSecondRow(
                onDateSelected = { date ->
                    date?.let { safeDate ->
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateEventSecondRowDate(
                                safeDate.toLocalDateTime()
                            )
                        )
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateShouldShowSecondRowDatePicker(
                                true
                            )
                        )
                    }
                },
                onDismiss = {
                    onUpdateState(
                        AgendaDetailStateUpdate.UpdateShouldShowSecondRowDatePicker(
                            false
                        )
                    )
                },
                initialDate = state.secondRowDate?.toLong()
            )
        }
    }
}

@Composable
fun AddPhotosSection(
    isReadOnly: Boolean,
    photos: List<Photo>,
    onAddPhotos: () -> Unit,
    selectedImageUri: Uri?,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    isEventCreator: Boolean
) {

    val context = LocalContext.current

    selectedImageUri?.let { uri ->
        if (photos.none { it.url == uri.toString() }) {
            onAction(AgendaDetailAction.OnPhotoCompress(uri))
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(colors.light2)
            .padding(dimensions.default16dp),
        horizontalArrangement = if (photos.isEmpty() || isReadOnly) Arrangement.Center else Arrangement.spacedBy(
            dimensions.small8dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isReadOnly) {
            items(photos.take(MAX_NUMBER_OF_PHOTOS), key = { photo -> photo.key }) { photo ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.lightBlue, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(photo.url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onAction(AgendaDetailAction.OnPhotoPressed(photo.key))
                            }
                    )
                }
            }
            if (photos.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable { onAddPhotos() }
                            .padding(vertical = dimensions.default16dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add photos",
                            tint = colors.gray
                        )
                        Spacer(modifier = Modifier.width(dimensions.small8dp))
                        Text(stringResource(R.string.Add_photos), color = colors.gray)
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, colors.lightBlue, RoundedCornerShape(10.dp))
                            .clickable {
                                if (photos.size < MAX_NUMBER_OF_PHOTOS) {
                                    onAddPhotos()
                                } else {
                                    showToast(context, R.string.max_number_of_photos)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add photos",
                            tint = colors.lightBlue
                        )
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { onAddPhotos() }
                        .padding(vertical = dimensions.default16dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add photos",
                        tint = colors.gray
                    )
                    Spacer(modifier = Modifier.width(dimensions.small8dp))
                    Text(stringResource(R.string.Add_photos), color = colors.gray)
                }
            }
        }
    }
}

@Composable
fun SetReminderRow(
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    state: AgendaDetailState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (state.isReadOnly) {
                    Modifier
                } else {
                    Modifier.clickable {
                        onUpdateState(AgendaDetailStateUpdate.UpdateShouldShowReminderDropdown(true))
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (state.isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
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
                text = when (state.selectedReminder.milliseconds) {
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

        if (!state.isReadOnly) {
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
                val selectedTime = state.time
                val remindAt = (selectedTime.toLong() ?: 0) - it.timeBeforeInMillis
                Timber.d("DDD component - onItemSelected: remindAt - ${remindAt.toLocalDateTime()} | selectedTime - $selectedTime")
                onUpdateState(AgendaDetailStateUpdate.UpdateSelectedReminder(it.timeBeforeInMillis))
                onUpdateState(
                    AgendaDetailStateUpdate.UpdateRemindAtTime(remindAt.toLocalDateTime())
                )
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

@Preview
@Composable
fun AddPhotosSectionEditablePreview() {
    AppTheme {
        AddPhotosSection(
            selectedImageUri = Uri.EMPTY,
            onAddPhotos = {},
            isReadOnly = false,
            onUpdateState = {},
            photos = listOf(
                Photo("dfdff", "https://picsum.photos/200"),
                Photo("fdfdffeferf", "https://picsum.photos/200")
            ),
            onAction = {},
            isEventCreator = false
        )
    }
}

@Preview
@Composable
fun AddPhotosSectionReadOnlyPreview() {
    AppTheme {
        AddPhotosSection(
            selectedImageUri = Uri.EMPTY,
            onAddPhotos = {},
            isReadOnly = true,
            onUpdateState = {},
            photos = emptyList(),
            onAction = {},
            isEventCreator = false
        )
    }
}

// Some issues here with material 3 due to timepickerstate
//@Preview
//@Composable
//fun TimeAndDateRowPreview() {
//    AppTheme {
//        TimeAndDateRow(
//            agendaItem = AgendaItem(
//                id = "suavitate",
//                title = "homero",
//                description = "congue",
//                time = LocalDateTime.now(),
//                remindAt = LocalDateTime.now(),
//                details = AgendaItemDetails.Task(isDone = false)
//            ),
//            text = "From",
//            onUpdateState = {},
//            state = AgendaDetailState()
//        )
//    }
//}