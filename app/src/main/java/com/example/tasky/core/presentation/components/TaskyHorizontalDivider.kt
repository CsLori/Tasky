package com.example.tasky.core.presentation.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasky.ui.theme.AppTheme.colors

@Composable
fun DefaultHorizontalDivider(modifier: Modifier = Modifier) = HorizontalDivider(modifier = modifier, color = colors.light, thickness = 1.dp)
