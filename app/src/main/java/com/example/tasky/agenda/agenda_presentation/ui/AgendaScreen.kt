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
import com.example.tasky.agenda.agenda_presentation.components.DatePickerModal
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaViewModel
import com.example.tasky.agenda.util.AgendaDetailOption
import com.example.tasky.agenda.util.AgendaDropdown
import com.example.tasky.core.presentation.components.AgendaDetailDropdown
import com.example.tasky.core.presentation.components.AgendaDropdown
import com.example.tasky.core.presentation.components.LogoutDropdown
import com.example.tasky.core.util.DateUtils
import com.example.tasky.core.util.DateUtils.getCurrentMonth
import com.example.tasky.core.util.DateUtils.getDaysWithDates
import com.example.tasky.core.util.DateUtils.localDateToStringddMMMMyyyyFormat
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
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
        onSelectedDate = { agendaViewModel.setSelectedDate(it) },
        selectedDate = state.selectedDate
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
private fun AgendaContent(
    onAgendaDetailPressed: () -> Unit,
    onSelectedDate: (LocalDate) -> Unit,
    selectedDate: LocalDate
) {
    var itemSelected: AgendaDropdown? by remember { mutableStateOf(null) }
    var visible by remember { mutableStateOf(false) }
    var shouldShowDatePicker by remember { mutableStateOf(false) }
    var month by remember { mutableStateOf("") }
    var date by remember { (mutableStateOf<LocalDate>(LocalDate.now())) }
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            containerColor = colors.black,
            onClick = { visible = !visible },
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
                    itemSelected = agendaItem
                    onAgendaDetailPressed()
                    when (agendaItem) {
                        AgendaDropdown.TASK -> {}
                        AgendaDropdown.EVENT -> {}
                        AgendaDropdown.REMINDER -> {}
                    }
                },
                selectedItem = itemSelected,
                visible = visible
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
                                shouldShowDatePicker = !shouldShowDatePicker
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = month.ifEmpty { getCurrentMonth() },
                            style = typography.title,
                            textAlign = TextAlign.Center,
                            color = colors.white,
                            modifier = Modifier.clickable {
                                shouldShowDatePicker = !shouldShowDatePicker
                            }
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "",
                            tint = colors.white
                        )
                    }

                    if (shouldShowDatePicker) {
                        DatePickerModal(
                            onDateSelected = { date ->
                                date?.let { safeDate ->
                                    val result = DateUtils.convertMillisToLocalDate(safeDate)
                                    month = result.month.toString()
                                    onSelectedDate(DateUtils.longToLocalDate(safeDate))
                                }
                            },
                            onDismiss = {
                                shouldShowDatePicker = false
                            },
                            initialDate = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                                .toEpochMilli()
                        )
                    }
                    UserInitialsButton()
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

                CalendarDays(selectedDate)

                Text(
                    if (selectedDate == LocalDate.now()) "Today" else selectedDate.localDateToStringddMMMMyyyyFormat(),
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
fun CalendarDays(date: LocalDate) {
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
                    isSelected = index == 0
                )
            }
        }
    }
}

@Composable
private fun CalendarComponent(dayLetter: String, dayNumber: String, isSelected: Boolean = false) {
    val componentHeight = 61.dp
    val componentWidth = 40.dp
    Column(
        modifier = Modifier
            .height(componentHeight)
            .width(componentWidth)
            .clip(RoundedCornerShape(dimensions.large24dp))
            .background(if (isSelected) colors.orange else colors.white),
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
fun UserInitialsButton() {
    var visible by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = colors.light,
        onClick = { visible = !visible }
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
        if (visible) {
            LogoutDropdown(
                onItemSelected = {},
                visible = visible,
                onDismiss = { visible = false }
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
            onSelectedDate = {},
            selectedDate = LocalDate.now()
        )
    }
}
