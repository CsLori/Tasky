package com.example.tasky.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.agenda.agenda_presentation.components.AgendaDetailOption
import com.example.tasky.agenda.agenda_presentation.components.AgendaOption
import com.example.tasky.agenda.agenda_presentation.components.ReminderOption
import com.example.tasky.core.presentation.animation.AnimateDropdownMenu
import com.example.tasky.ui.theme.AppTheme.colors

@Composable
fun AgendaDropdown(
    modifier: Modifier = Modifier,
    listItems: List<AgendaOption> = emptyList(),
    onItemSelected: (AgendaOption) -> Unit,
    visible: Boolean?,
) {
    var interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    var expanded by remember { mutableStateOf(false) }
    var itemIndex: Int
    val lastItem = listItems.size - 1

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = true
                }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimateDropdownMenu(targetState = visible) {
            DropdownMenu(
                modifier = Modifier
                    .background(colors.white)
                    .clip(RoundedCornerShape(7.dp)),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (25).dp, y = 0.dp),

                ) {
                listItems.forEachIndexed { index, listItem ->
                    val rowBackgroundColor = colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(listItem)
                            expanded = false
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(listItem.displayName) },
                    )
                    if (itemIndex != lastItem)
                        DefaultHorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun LogoutDropdown(
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimateDropdownMenu(targetState = visible) {
            DropdownMenu(
                modifier = Modifier
                    .background(colors.white)
                    .clip(RoundedCornerShape(7.dp)),
                expanded = visible,
                onDismissRequest = { onDismiss() },
                offset = DpOffset(x = (50).dp, y = 0.dp),

                ) {

                DropdownMenuItem(
                    onClick = {
                        onItemSelected()
                    },
                    modifier = Modifier.background(colors.white),
                    text = { Text("Logout") },
                )
            }
        }
    }
}

@Composable
fun AgendaDetailDropdown(
    modifier: Modifier = Modifier,
    options: List<AgendaDetailOption>,
    onItemSelected: (AgendaDetailOption) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    var itemIndex: Int
    val lastItem = options.size - 1

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimateDropdownMenu(targetState = visible) {
            DropdownMenu(
                modifier = Modifier
                    .background(colors.white)
                    .clip(RoundedCornerShape(7.dp)),
                expanded = visible,
                onDismissRequest = { onDismiss() },
                offset = DpOffset(x = (50).dp, y = 0.dp),

                ) {
                options.forEachIndexed { index, option ->
                    val rowBackgroundColor = colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(option)
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(option.option) },
                    )
                    if (itemIndex != lastItem)
                        DefaultHorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ReminderDropdown(
    modifier: Modifier = Modifier,
    options: List<ReminderOption>,
    onItemSelected: (ReminderOption) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    var itemIndex: Int
    val lastItem = options.size - 1

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimateDropdownMenu(targetState = visible) {
            DropdownMenu(
                modifier = Modifier
                    .background(colors.white)
                    .clip(RoundedCornerShape(7.dp)),
                expanded = visible,
                onDismissRequest = { onDismiss() },
                offset = DpOffset(x = (50).dp, y = 0.dp),

                ) {
                options.forEachIndexed { index, option ->
                    val rowBackgroundColor = colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(option)
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(option.label) },
                    )
                    if (itemIndex != lastItem)
                        DefaultHorizontalDivider()
                }
            }
        }
    }
}