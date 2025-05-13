@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditState
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.action.AgendaItemEditAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography

@Composable
internal fun AgendaItemEditScreen(
    agendaItemEditViewModel: AgendaItemEditViewModel = hiltViewModel(),
    title: String,
    description: String,
    editType: EditType,
    onBackPressed: () -> Unit,
    onSavePressed: (String, String) -> Unit
) {
    LaunchedEffect(description) {
        agendaItemEditViewModel.onAction(
            AgendaItemEditAction.OnUpdateDescription(description)
        )
    }

    LaunchedEffect(title) {
        agendaItemEditViewModel.onAction(
            AgendaItemEditAction.OnUpdateTitle(title)
        )
    }

    val state = agendaItemEditViewModel.state.collectAsState().value
    AgendaItemEditContent(
        state = state,
        editType = editType,
        onAction = { action ->
            when (action) {
                AgendaItemEditAction.OnNavigateBack -> onBackPressed()
                AgendaItemEditAction.OnSaveAgendaItem -> onSavePressed(
                    state.title,
                    state.description
                )

                else -> Unit
            }
            agendaItemEditViewModel.onAction(action)
        }
    )
}

@Composable
private fun AgendaItemEditContent(
    state: AgendaItemEditState,
    editType: EditType,
    onAction: (AgendaItemEditAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
    ) {
        Header(
            editType = editType,
            onAction = onAction,
            state = state
        )

        if (editType == EditType.TITLE) {
            Textarea(
                onUpdate = { updatedText ->
                    onAction(
                        AgendaItemEditAction.OnUpdateTitle(updatedText)
                    )
                },
                textValue = state.title,
                maxLines = 2
            )
        } else {
            Textarea(
                onUpdate = { updatedText ->
                    onAction(
                        AgendaItemEditAction.OnUpdateDescription(updatedText)
                    )
                },
                textValue = state.description,
                maxLines = 2
            )
        }
    }
}

@Composable
fun Header(
    editType: EditType,
    onAction: (AgendaItemEditAction) -> Unit,
    state: AgendaItemEditState
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.default16dp, vertical = dimensions.large32dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onAction(AgendaItemEditAction.OnNavigateBack) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = colors.black
                )
            }

            val title =
                if (editType == EditType.TITLE) EditType.TITLE.name else EditType.DESCRIPTION.name
            Text(
                text = "Edit $title".uppercase(),
                style = typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    lineHeight = 12.sp
                ),
                textAlign = TextAlign.Center,
                color = colors.black,
            )

            Text(
                text = "Save",
                style = typography.bodyLarge.copy(
                    fontWeight = FontWeight.W600,
                    lineHeight = 12.sp,
                    color = colors.green
                ),
                modifier = Modifier.clickable { onAction(AgendaItemEditAction.OnSaveAgendaItem) }
            )
        }
    }
}


@Composable
fun Textarea(
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppTheme.colors.transparent,
        unfocusedBorderColor = AppTheme.colors.transparent,
        disabledBorderColor = AppTheme.colors.transparent,
        focusedContainerColor = AppTheme.colors.white,
        unfocusedContainerColor = AppTheme.colors.white,
        errorContainerColor = AppTheme.colors.white,
    ),
    textStyle: TextStyle = typography.title.copy(
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    onUpdate: (String) -> Unit,
    textValue: String,
    maxLines: Int
) {
    TextField(
        value = textValue,
        onValueChange = { newValue -> onUpdate(newValue) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(100.dp, 200.dp),
        colors = colors,
        maxLines = maxLines,
        textStyle = textStyle,
    )
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaItemEditPreview() {
    AppTheme {
        AgendaItemEditContent(
            state = AgendaItemEditState(
                "Titlevcvcvcvcvcvvcvcvcvcvcvcvcvcvcvcvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv",
                "Edit this title"
            ),
            editType = EditType.TITLE,
            onAction = {}
        )
    }
}