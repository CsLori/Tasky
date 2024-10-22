@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasky.agenda.agenda_presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditAction
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditState
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditUpdate
import com.example.tasky.agenda.agenda_presentation.viewmodel.AgendaItemEditViewModel
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.EditType
import com.example.tasky.core.presentation.components.DefaultHorizontalDivider
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
internal fun AgendaItemEditScreen(
    agendaItemEditViewModel: AgendaItemEditViewModel,
    title: String,
    description: String,
    editType: EditType,
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    LaunchedEffect(Unit) {
        agendaItemEditViewModel.updateState(
            AgendaItemEditUpdate.UpdateDescription(
                description
            )
        )
        agendaItemEditViewModel.updateState(
            AgendaItemEditUpdate.UpdateTitle(
                title
            )
        )
    }

    val state = agendaItemEditViewModel.state.collectAsState().value
    AgendaItemEditContent(
        state = state,
        editType = editType,
        onUpdate = { action -> agendaItemEditViewModel.updateState(action) },
        onAction = { action ->
            when (action) {
                AgendaItemEditAction.OnBackPressed -> onBackPressed()
                AgendaItemEditAction.OnSavePressed -> onSavePressed()
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
private fun AgendaItemEditContent(
    state: AgendaItemEditState,
    editType: EditType,
    onUpdate: (AgendaItemEditUpdate) -> Unit,
    onAction: (AgendaItemEditAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white),
    ) {
        Header(
            editType = editType,
            onSavePressed = { onAction(AgendaItemEditAction.OnSavePressed) },
            onBackPressed = { onAction(AgendaItemEditAction.OnBackPressed) }
        )

        if (editType == EditType.TITLE) {
            Textarea(
                onUpdate = { updatedText ->
                    onUpdate(
                        AgendaItemEditUpdate.UpdateTitle(updatedText)
                    )
                },
                textValue = state.title,
            )

        } else {
            Textarea(
                onUpdate = { updatedText ->
                    onUpdate(
                        AgendaItemEditUpdate.UpdateDescription(updatedText)
                    )
                },
                textValue = state.description,
            )
        }

    }
}

@Composable
private fun Header(
    onSavePressed: () -> Unit,
    onBackPressed: () -> Unit,
    editType: EditType
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
                onClick = { onBackPressed() },
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
                modifier = Modifier.clickable { onSavePressed() }
            )

        }
    }
    DefaultHorizontalDivider(modifier = Modifier.padding(horizontal = dimensions.default16dp))
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
        fontSize = 26.sp,
        lineHeight = 12.sp
    ),
    onUpdate: (String) -> Unit,
    textValue: String,
) {
    TextField(
        value = textValue,
        onValueChange = { newValue -> onUpdate(newValue) },
        modifier = Modifier
            .fillMaxWidth(),
        colors = colors,
        maxLines = 3,
        textStyle = textStyle,
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun AgendaItemEditPreview() {
    AppTheme {
        AgendaItemEditContent(
            state = AgendaItemEditState("Title", "Edit this title"),
            editType = EditType.TITLE,
            onAction = {},
            onUpdate = {}
        )
    }
}