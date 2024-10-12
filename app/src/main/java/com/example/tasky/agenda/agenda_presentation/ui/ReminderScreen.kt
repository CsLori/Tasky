package com.example.tasky.agenda.agenda_presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tasky.ui.theme.AppTheme


@Composable
internal fun ReminderScreen() {
    ReminderContent()
}

@Composable
private fun ReminderContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reminder Screen", style = AppTheme.typography.bodyMedium)
    }
}