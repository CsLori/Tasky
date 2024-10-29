@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.tasky.agenda.agenda_domain.model.ReminderType
import com.example.tasky.agenda.agenda_presentation.components.AddPhotosSection
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemDescription
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemMainHeader
import com.example.tasky.agenda.agenda_presentation.components.AgendaItemTitle
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.components.SetReminderRow
import com.example.tasky.agenda.agenda_presentation.components.TimeAndDateRow
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaDetailViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaDetailAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailState
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography


@Composable
internal fun AgendaDetailScreen(
    agendaDetailViewModel: AgendaDetailViewModel,
    onNavigateToAgendaScreen: () -> Unit,
    onClose: () -> Boolean,
    onEditPressed: () -> Unit,
    agendaItemId: String? = null,
) {
    val state = agendaDetailViewModel.state.collectAsStateWithLifecycle().value
    val uiState = agendaDetailViewModel.uiState.collectAsStateWithLifecycle().value

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
            agendaDetailViewModel.updateState(
                AgendaDetailStateUpdate.UpdateSelectedAgendaItem(
                    agendaDetailViewModel.loadTask(agendaItemId)
                )
            )
        }
    }

    AgendaDetailContent(
        state = state,
        uiState = uiState,
        onUpdateState = { action -> agendaDetailViewModel.updateState(action) },
        onAction = { action ->
            when (action) {
                AgendaDetailAction.OnClosePressed -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnCreateSuccess -> onNavigateToAgendaScreen()
                AgendaDetailAction.OnEditRowPressed -> onEditPressed()
                AgendaDetailAction.OnSavePressed -> {
                    if (agendaItemId == null) {
                        agendaDetailViewModel.createTask()
                    } else {
                        agendaDetailViewModel.updateTask(state.task)
                    }
                    onNavigateToAgendaScreen()
                }

                AgendaDetailAction.OnEnableEditPressed -> agendaDetailViewModel.updateState(
                    AgendaDetailStateUpdate.UpdateIsReadOnly(false)
                )
            }
        },
        agendaItemId = agendaItemId,
        agendaItem = state.selectedAgendaItem
    )
}

@Composable
private fun AgendaDetailContent(
    state: AgendaDetailState,
    uiState: AgendaDetailViewModel.AgendaDetailUiState,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
    agendaItemId: String?,
    agendaItem: AgendaItem?
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
                            agendaItem = agendaItem,
                            onUpdateState = onUpdateState,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: AgendaDetailState,
    agendaItem: AgendaItem?,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
    onAction: (AgendaDetailAction) -> Unit,
) {
    AgendaItemMainHeader(agendaItem)

    AgendaItemTitle(agendaItem, onUpdateState, onAction, state)

    AgendaItemDescription(agendaItem, onUpdateState, onAction, state)

    TimeAndDateRow(onUpdateState, state)
    TimeAndDateRow(onUpdateState, state)
    if (agendaItem is AgendaItem.Event) {
        AddPhotosSection({})
    }

    DefaultHorizontalDivider()

    SetReminderRow(onUpdateState, state)

    if (agendaItem is AgendaItem.Event) {
        VisitorsSection(listOf("Lori", "Gyuri", " Henrik"), {})
    }
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
private fun VisitorsSection(
    visitors: List<String>,
    onVisitorStatusChanged: () -> Unit
) {
    Column {
        Text("Visitors")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .requiredWidth(100.dp)
                    .requiredHeight(35.dp)
                    .clip(RoundedCornerShape(100.dp)),
                colors = ButtonDefaults.buttonColors().copy(containerColor = colors.black),
                onClick = { },
                content = {
                    Text(
                        "All",
                        style = typography.bodySmall.copy(
                            fontWeight = FontWeight.W500,
                            lineHeight = 15.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = colors.white
                    )
                }
            )
            Button(
                modifier = Modifier
                    .requiredWidth(110.dp)
                    .requiredHeight(35.dp)
                    .clip(RoundedCornerShape(100.dp)),
                colors = ButtonDefaults.buttonColors().copy(containerColor = colors.light2),
                onClick = { },
                content = {
                    Text(
                        "Going", style = typography.bodySmall.copy(
                            fontWeight = FontWeight.W500,
                            lineHeight = 15.sp
                        ), textAlign = TextAlign.Center,
                        color = colors.black
                    )
                }
            )
            Button(
                modifier = Modifier
                    .requiredWidth(110.dp)
                    .requiredHeight(35.dp)
                    .clip(RoundedCornerShape(100.dp)),
                colors = ButtonDefaults.buttonColors().copy(containerColor = colors.light2),
                onClick = { },
                content = {
                    Text(
                        "Not going", style = typography.bodySmall.copy(
                            fontWeight = FontWeight.W500,
                            lineHeight = 15.sp
                        ), textAlign = TextAlign.Center,
                        color = colors.black
                    )
                }
            )
        }

        LazyColumn {
            items(visitors.size) { visitor ->
//                VisitorItem(
//                    visitor = visitor,
//                    onStatusChanged = onVisitorStatusChanged
//                )
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
                    text = "Save", color = colors.white
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
            agendaItem = AgendaItem.Task(
                taskId = "persius",
                taskTitle = "mauris",
                taskDescription = null,
                time = 7622,
                isDone = false,
                remindAtTime = 2214,
                taskReminderType = ReminderType.TASK
            )
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
            agendaItem = AgendaItem.Task(
                taskId = "persius",
                taskTitle = "mauris",
                taskDescription = null,
                time = 7622,
                isDone = false,
                remindAtTime = 2214,
                taskReminderType = ReminderType.TASK
            )
        )
    }
}