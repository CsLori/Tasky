@file:Suppress("OPT_IN_USAGE_FUTURE_ERROR")

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.model.Attendee
import com.example.tasky.agenda.agenda_domain.model.Photo
import com.example.tasky.agenda.agenda_presentation.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.state.AgendaDetailStateUpdate
import com.example.tasky.agenda.agenda_presentation.state.EditType
import com.example.tasky.agenda.agenda_presentation.state.RemindBeforeDuration
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState,
    isEventCreator: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensions.default16dp)
            .then(if (!state.isReadOnly && isEventCreator) {
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
        if (!state.isReadOnly && isEventCreator) {
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
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    state: AgendaDetailState,
    isEventCreator: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (!state.isReadOnly && isEventCreator) {
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
        if (!state.isReadOnly && isEventCreator) {
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
    endTime: Boolean = false,
    isEventCreator: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        val firstRowTimePickerState by remember {
            mutableStateOf(
                TimePickerState(
                    initialHour = state.time.hour,
                    initialMinute = state.time.minute,
                    is24Hour = true
                )
            )
        }

        val toTime =
            ((state.details as? AgendaItemDetails.Event)?.toTime?.toLong()
                ?.plus(thirtyMinutesInMillis))?.toLocalDateTime()
                ?: (LocalDateTime.now().toLong() + thirtyMinutesInMillis).toLocalDateTime()
        val secondRowTimePickerState by remember {
            mutableStateOf(
                TimePickerState(
                    initialHour = toTime.hour,
                    initialMinute = toTime.minute,
                    is24Hour = true
                )
            )
        }

        val selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate =
                    Instant.ofEpochMilli(utcTimeMillis).atZone((ZoneId.systemDefault()))
                        .toLocalDate()
                return !selectedDate.isBefore(LocalDate.now())
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }

        var shouldShowStartDatePicker by remember { mutableStateOf(false) }
        var shouldShowEndDatePicker by remember { mutableStateOf(false) }
        var shouldShowStartTimePicker by remember { mutableStateOf(false) }
        var shouldShowEndTimePicker by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .width(120.dp)
                .weight(1f)
                .then(
                    if (!state.isReadOnly && isEventCreator) {
                        Modifier.clickable {
                            if (endTime) {
                                shouldShowEndTimePicker = !shouldShowEndTimePicker

                            } else {
                                shouldShowStartTimePicker = !shouldShowStartTimePicker
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
                        is AgendaItemDetails.Task -> state.time.toHourMinuteFormat()
                        is AgendaItemDetails.Event -> {
                            if (endTime) (state.details as? AgendaItemDetails.Event)?.toTime?.toHourMinuteFormat()
                                ?: (LocalDateTime.now()
                                    .toLong() + thirtyMinutesInMillis).toLocalDateTime()
                                    .toHourMinuteFormat() else state.time.toHourMinuteFormat()
                        }

                        is AgendaItemDetails.Reminder -> state.time.toHourMinuteFormat()
                    },

                    style = typography.bodyLarge.copy(
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.W400
                    )
                )

                if (!state.isReadOnly && isEventCreator) {
                    Icon(
                        modifier = Modifier.padding(end = dimensions.default16dp),
                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                        contentDescription = "Navigate next"
                    )
                }
            }

            if (shouldShowStartTimePicker && !endTime) {
                TimePickerDialog(
                    onDismissRequest = { shouldShowStartTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedHour = firstRowTimePickerState.hour
                                val selectedMinute = firstRowTimePickerState.minute

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateStartTime(
                                        selectedMinute, selectedHour
                                    )
                                )
                                shouldShowStartTimePicker = false
                            }
                        ) { Text(stringResource(R.string.OK)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { shouldShowStartTimePicker = false }
                        ) { Text(stringResource(R.string.Cancel)) }
                    },
                    content = {
                        TimePicker(state = firstRowTimePickerState)
                    }
                )
            }

            if (shouldShowEndTimePicker) {
                TimePickerDialogSecondRow(
                    onDismissRequest = { shouldShowEndTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedHour = secondRowTimePickerState.hour
                                val selectedMinute = secondRowTimePickerState.minute

                                onUpdateState(
                                    AgendaDetailStateUpdate.UpdateEndTime(
                                        selectedMinute, selectedHour
                                    )
                                )
                                shouldShowEndTimePicker = false
                            }
                        ) { Text(stringResource(R.string.OK)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { shouldShowEndTimePicker = false }
                        ) { Text(stringResource(R.string.Cancel)) }
                    },
                    content = {
                        TimePicker(state = secondRowTimePickerState)

                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (!state.isReadOnly && isEventCreator) {
                        Modifier.clickable {
                            if (endTime) {
                                shouldShowEndDatePicker = !shouldShowEndDatePicker
                            } else {
                                shouldShowStartDatePicker = !shouldShowStartDatePicker
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
                text = if (endTime) {
                    (state.details as? AgendaItemDetails.Event)?.toTime?.toStringMMMdyyyyFormat()
                        ?: LocalDateTime.now().toStringMMMdyyyyFormat()
                } else state.time.toStringMMMdyyyyFormat(),
                style = typography.bodyLarge.copy(lineHeight = 15.sp, fontWeight = FontWeight.W400)
            )
            if (!state.isReadOnly && isEventCreator) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Navigate next"
                )
            }
        }

        if (shouldShowStartDatePicker) {
            DatePickerModalFirstRow(
                onDateSelected = { date ->
                    date?.let { safeDate ->
                        val localDateTime = safeDate.toLocalDateTime()

                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateStartDay(
                                localDateTime.dayOfMonth,
                                localDateTime.monthValue,
                                localDateTime.year
                            )
                        )
                        shouldShowStartDatePicker = true
                    }
                },
                onDismiss = {
                    shouldShowStartDatePicker = false
                },
                initialDate = state.time.toLong(),
                selectableDates = selectableDates
            )
        }

        if (shouldShowEndDatePicker) {
            DatePickerModalSecondRow(
                onDateSelected = { date ->
                    date?.let { safeDate ->
                        val localDateTime = safeDate.toLocalDateTime()

                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateEndDay(
                                localDateTime.dayOfMonth,
                                localDateTime.monthValue,
                                localDateTime.year
                            )
                        )
                        shouldShowEndDatePicker = true
                    }
                },
                onDismiss = {
                    shouldShowEndDatePicker = false

                },
                initialDate = (state.details as? AgendaItemDetails.Event)?.toTime?.toLong(),
                selectableDates = selectableDates
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
        horizontalArrangement = if (!isReadOnly || (isReadOnly && photos.isNotEmpty())) Arrangement.spacedBy(
            dimensions.small8dp
        ) else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isReadOnly || (isReadOnly && photos.isNotEmpty())) {
            items(photos.take(MAX_NUMBER_OF_PHOTOS), key = { photo -> photo.key }) { photo ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.lightBlue, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val painter = rememberAsyncImagePainter(photo.url)

                    if (painter.state is AsyncImagePainter.State.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(3.dp),
                            color = colors.green
                        )
                    }

                    Image(
                        painter = painter,
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
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.lightBlue, RoundedCornerShape(10.dp))
                        .then(
                            if (isEventCreator) {
                                Modifier.clickable {
                                    if (photos.size < MAX_NUMBER_OF_PHOTOS) {
                                        if (!isReadOnly) {
                                            onAddPhotos()
                                        }
                                    } else {
                                        showToast(context, R.string.max_number_of_photos)
                                    }
                                }
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add photos",
                        tint = colors.lightBlue
                    )
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
    var shouldShowReminderDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.default16dp)
            .then(
                if (state.isReadOnly) {
                    Modifier
                } else {
                    Modifier.clickable {
                        shouldShowReminderDropdown = true
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (state.isReadOnly) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Rounded.Square,
                    tint = colors.light2,
                    contentDescription = "Rounded Square"
                )
                Icon(
                    modifier = Modifier.size(30.dp),
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
    if (shouldShowReminderDropdown) {
        ReminderDropdown(
            options = reminderOptions,
            onItemSelected = {
                val selectedTime = state.time
                val remindAt = (selectedTime.toLong() ?: 0) - it.timeBeforeInMillis
                onUpdateState(AgendaDetailStateUpdate.UpdateSelectedReminder(it.timeBeforeInMillis))
                onUpdateState(
                    AgendaDetailStateUpdate.UpdateRemindAtTime(remindAt.toLocalDateTime())
                )
                shouldShowReminderDropdown = false
            },
            visible = shouldShowReminderDropdown,
            onDismiss = {
                shouldShowReminderDropdown = false
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

@Preview
@Composable
fun TimeAndDateRowPreview() {
    AppTheme {
        TimeAndDateRow(
            agendaItem = AgendaItem(
                id = "suavitate",
                title = "homero",
                description = "congue",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now(),
                details = AgendaItemDetails.Task(isDone = false)
            ),
            text = "From",
            onUpdateState = {},
            state = AgendaDetailState(),
            isEventCreator = false
        )
    }
}

@Preview
@Composable
fun TimeAndDateRowEventCreatorPreview() {
    AppTheme {
        TimeAndDateRow(
            agendaItem = AgendaItem(
                id = "suavitate",
                title = "homero",
                description = "congue",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now(),
                details = AgendaItemDetails.Event(
                    toTime = LocalDateTime.now(),
                    attendees = listOf(
                        Attendee(
                            email = "julio.velasquez@example.com",
                            name = "Myles Orr",
                            userId = "amet",
                            eventId = "melius",
                            isGoing = false,
                            remindAt = 3775,
                            isCreator = false
                        )
                    ),
                    photos = listOf(),
                    isUserEventCreator = true,
                    host = "contentiones"
                )
            ),
            text = "From",
            onUpdateState = {},
            state = AgendaDetailState(),
            isEventCreator = true
        )
    }
}

@Preview
@Composable
fun SetReminderRowPreview() {
    AppTheme {
        SetReminderRow(
            onUpdateState = {},
            state = AgendaDetailState()
        )
    }
}