package com.example.tasky.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.agenda.util.AgendaDetailOption
import com.example.tasky.core.presentation.animation.AnimateDropdownMenu
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.typography

@Composable
fun <T> AgendaDropdown(
    modifier: Modifier = Modifier,
    listItems: List<T> = emptyList(),
    onItemSelected: (T) -> Unit,
    selectedItem: T?,
    visible: Boolean?,
) {
    val interactionSource = MutableInteractionSource()
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
//        Text(
//            text = "",
//        )

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
                    val rowBackgroundColor =
                        if (listItems.indexOf(selectedItem) == index) colors.light2 else colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(listItem)
                            expanded = false
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(listItem.toString()) },
                    )
                    if (itemIndex != lastItem)
                        HorizontalDivider(color = colors.light, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun <T> AgendaMonthDropdown(
    modifier: Modifier = Modifier,
    listItems: List<T> = emptyList(),
    onItemSelected: (T) -> Unit,
    selectedItem: T,
    visible: Boolean?,
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
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
        Text(
            text = selectedItem.toString().uppercase(),
            style = typography.title,
            textAlign = TextAlign.Center,
            color = colors.white,
        )

        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "",
            tint = colors.white
        )

        AnimateDropdownMenu(targetState = visible) {
            DropdownMenu(
                modifier = Modifier
                    .background(colors.white)
                    .clip(RoundedCornerShape(7.dp)),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (-150).dp, y = 0.dp),

                ) {
                listItems.forEachIndexed { index, listItem ->
                    val rowBackgroundColor =
                        if (listItems.indexOf(selectedItem) == index) colors.light2 else colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(listItem)
                            expanded = false
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(listItem.toString()) },
                    )
                    if (itemIndex != lastItem)
                        HorizontalDivider(color = colors.light, thickness = 1.dp)
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
fun <T> AgendaDetailDropdown(
    modifier: Modifier = Modifier,
    options: List<T>,
    onItemSelected: (T) -> Unit,
    selectedItem: T?,
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
                    val rowBackgroundColor =
                        if (options.indexOf(selectedItem) == index) colors.light2 else colors.white
                    itemIndex = index
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(option)
                        },
                        modifier = Modifier.background(rowBackgroundColor),
                        text = { Text(option.toString()) },
                    )
                    if (itemIndex != lastItem)
                        HorizontalDivider(color = colors.light, thickness = 1.dp)
                }
            }
        }
    }
}

