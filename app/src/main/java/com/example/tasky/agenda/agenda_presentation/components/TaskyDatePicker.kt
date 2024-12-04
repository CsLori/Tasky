package com.example.tasky.agenda.agenda_presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tasky.ui.theme.AppTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Long?
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalFirstRow(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Long?,
    selectableDates: SelectableDates
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate,
            yearRange = LocalDateTime.now().year..3000,
            selectableDates = selectableDates
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton =
        {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton =
        {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
    {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalSecondRow(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Long?,
    selectableDates: SelectableDates
) {

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate,
            yearRange = LocalDateTime.now().year..3000,
            selectableDates = selectableDates
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DatePickerModalPreview() {
    AppTheme {
        DatePickerModal(
            onDateSelected = {},
            onDismiss = {},
            initialDate = 2133444,
        )
    }
}