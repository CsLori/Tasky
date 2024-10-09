package com.example.tasky.agenda.agenda_presentation.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.tasky.ui.theme.AppTheme.colors

@Composable
internal fun AgendaScreen() {
    AgendaContent()
}

@Composable
private fun AgendaContent() {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            containerColor = colors.black,
            onClick = { },
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add button",
                tint = colors.white,
            )
        }
    }, floatingActionButtonPosition = FabPosition.End) { innerPadding ->

    }
}
