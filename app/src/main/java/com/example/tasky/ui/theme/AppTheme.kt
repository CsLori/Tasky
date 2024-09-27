package com.example.tasky.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

//
@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTypography provides AppTypography(),
        LocalColors provides AppColors(),
        LocalDimensions provides Dimensions()
    ) {
        content()
    }
}

object AppTheme {
    val typography: AppTypography
        @Composable
        get() = LocalTypography.current

    val colors: AppColors
        @Composable
        get() = LocalColors.current

    val dimensions: Dimensions
        @Composable
        get() = LocalDimensions.current
}


val LocalTypography =
    staticCompositionLocalOf<AppTypography> { error("No App Typography is provided") }
val LocalColors =
    staticCompositionLocalOf<AppColors> { error("No App Colors are provided") }
val LocalDimensions =
    staticCompositionLocalOf<Dimensions> { error("No App Dimensions are provided") }
