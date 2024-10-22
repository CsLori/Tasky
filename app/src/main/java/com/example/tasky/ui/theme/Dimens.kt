package com.example.tasky.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimensions (
    val extraSmall4dp: Dp = 4.dp,
    val small8dp: Dp = 8.dp,
    val default16dp: Dp = 16.dp,
    val large24dp: Dp = 24.dp,
    val large32dp: Dp = 32.dp,
    val extraLarge64dp: Dp = 64.dp,

    val cornerRadius30dp: Dp = 30.dp,
)