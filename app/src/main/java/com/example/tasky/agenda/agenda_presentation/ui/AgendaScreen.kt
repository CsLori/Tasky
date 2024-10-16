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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.agenda.agenda_presentation.components.AgendaDetailOption
import com.example.tasky.agenda.agenda_presentation.components.AgendaDropdown
import com.example.tasky.agenda.agenda_presentation.components.DatePickerModal
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.core.presentation.components.AgendaDetailDropdown
import com.example.tasky.core.presentation.components.AgendaDropdown
import com.example.tasky.core.presentation.components.LogoutDropdown
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.DateUtils
import com.example.tasky.util.DateUtils.getDaysWithDates
import com.example.tasky.util.DateUtils.localDateToStringddMMMMyyyyFormat
import java.time.LocalDate
import java.time.ZoneOffset

const val NUMBER_OF_DAYS_TO_SHOW = 5

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
internal fun AgendaScreen(
    agendaViewModel: AgendaViewModel,
    onAgendaDetailPressed: () -> Unit
) {

    val state = agendaViewModel.state.collectAsState().value

    AgendaContent(
        onAgendaDetailPressed = { onAgendaDetailPressed() },
        onUpdateState = { action -> agendaViewModel.updateState(action) },
        state = state,
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
private fun AgendaContent(
    onAgendaDetailPressed: () -> Unit,
    onUpdateState: (AgendaViewModel.AgendaAction) -> Unit,
    state: AgendaViewModel.AgendaState,
) {

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            containerColor = colors.black,
            onClick = {
                onUpdateState(AgendaViewModel.AgendaAction.UpdateVisibility(!state.isVisible))
            },
            shape = RoundedCornerShape(dimensions.default16dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add button",
                tint = colors.white,
            )
            AgendaDropdown(
                listItems = AgendaDropdown.entries,
                onItemSelected = { agendaItem ->
                    onUpdateState(AgendaViewModel.AgendaAction.UpdateItemSelected(agendaItem))
                    onAgendaDetailPressed()
                    when (agendaItem) {
                        AgendaDropdown.TASK -> {}
                        AgendaDropdown.EVENT -> {}
                        AgendaDropdown.REMINDER -> {}
                    }
                },
                selectedItem = state.itemSelected,
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
                                    AgendaViewModel.AgendaAction.UpdateShouldShowDatePicker(
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
                                    AgendaViewModel.AgendaAction.UpdateShouldShowDatePicker(
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
                                    val result = DateUtils.convertMillisToLocalDate(safeDate)
                                    onUpdateState(
                                        AgendaViewModel.AgendaAction.UpdateMonth(
                                            result.month.name
                                        )
                                    )

                                    onUpdateState(
                                        AgendaViewModel.AgendaAction.UpdateSelectedDate(
                                            DateUtils.longToLocalDate(safeDate)
                                        )
                                    )
                                    onUpdateState(
                                        AgendaViewModel.AgendaAction.UpdateIsDateSelectedFromDatePicker(
                                            true
                                        )
                                    )
                                }
                            },
                            onDismiss = {
                                onUpdateState(
                                    AgendaViewModel.AgendaAction.UpdateShouldShowDatePicker(
                                        false
                                    )
                                )
                            },
                            initialDate = state.selectedDate.atStartOfDay(ZoneOffset.UTC)
                                .toInstant()
                                .toEpochMilli()
                        )
                    }
                    UserInitialsButton(state = state, onUpdateState = onUpdateState)
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
                    onSelectedIndexChanged = { action ->
                        onUpdateState(AgendaViewModel.AgendaAction.UpdateSelectedIndex(action))
                    })

                Text(
                    if (state.selectedDate == LocalDate.now()) "Today" else state.selectedDate.localDateToStringddMMMMyyyyFormat(),
                    style = typography.calendarTitle,
                    modifier = Modifier.padding(vertical = dimensions.default16dp)
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(3) { TaskItem() }
                }
            }
        }
    }
}

@Composable
fun TaskItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(dimensions.default16dp))
            .height(150.dp)
            .background(colors.light2)
    ) {
        var visible by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .padding(dimensions.default16dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Icon checked"
                )

                Spacer(Modifier.width(dimensions.small8dp))

                Text(
                    "Project X",
                    style = typography.title.copy(lineHeight = 16.sp, fontSize = 20.sp)
                )
            }

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
                        }
                )
                if (visible) {
                    AgendaDetailDropdown(
                        options = AgendaDetailOption.entries,
                        onItemSelected = {},
                        selectedItem = AgendaDetailOption.OPEN,
                        visible = visible,
                        onDismiss = { visible = false },
                    )
                }
            }
        }
        Text(
            "Amet minim mollit non deserunt",
            modifier = Modifier.padding(start = 44.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = dimensions.default16dp, end = dimensions.default16dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text("Mar 5, 10:30 - Mar 5, 11:00")
        }
    }
    Spacer(modifier = Modifier.height(dimensions.default16dp))
}

@Composable
fun CalendarDays(
    date: LocalDate,
    selectedIndex: Int,
    isDateSelectedFromDatePicker: Boolean,
    onSelectedIndexChanged: (Int) -> Unit
) {
    val days = getDaysWithDates(date, NUMBER_OF_DAYS_TO_SHOW)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.default16dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, (dayLetter, dayNumber) ->
            if (dayLetter != null) {
                CalendarComponent(
                    dayLetter = dayLetter,
                    dayNumber = dayNumber.toString(),
                    isSelected = index == if (isDateSelectedFromDatePicker) 0 else selectedIndex,
                    onClick = {
                        onSelectedIndexChanged(index)
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
    state: AgendaViewModel.AgendaState,
    onUpdateState: (AgendaViewModel.AgendaAction) -> Unit
) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = colors.light,
        onClick = {
            onUpdateState(AgendaViewModel.AgendaAction.UpdateVisibility(!state.isVisible))
        }
    ) {
        Box(
            modifier = Modifier
                .background(colors.light),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LC",
                color = colors.lightBlue,
                style = typography.userButton
            )
        }
        if (state.isVisible) {
            LogoutDropdown(
                onItemSelected = {},
                visible = state.isVisible,
                onDismiss = {
                    onUpdateState(AgendaViewModel.AgendaAction.UpdateVisibility(false))
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun AgendaContentPreview() {
    AppTheme {
        AgendaContent(
            onAgendaDetailPressed = {},
            onUpdateState = {},
            state = AgendaViewModel.AgendaState()
        )
    }
}
