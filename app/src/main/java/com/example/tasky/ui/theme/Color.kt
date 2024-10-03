package com.example.tasky.ui.theme

import androidx.compose.ui.graphics.Color

//val green = Color(0xFF279F70)
//val black = Color(0xFF16161C)
//val white = Color(0xFFFFFFFF)
//val orange = Color(0xFFFDEFA8)
//
//val lightGreen = Color(0xFFAEF45C)
//val lightBlue = Color(0xFFB7C6DE)
//val light = Color(0xFFEEF6FF)
//val light2 = Color(0xFFF2F3F7)
//val border = Color(0xFFA1A4B2)

data class AppColors(
    val green: Color = Color(0xFF279F70),
    val black: Color = Color(0xFF16161C),
    val white: Color = Color(0xFFFFFFFF),
    val orange: Color = Color(0xFFFDEFA8),
    val gray: Color = Color(0xFFA1A4B2),
    val darkGray: Color = Color(0xFF5C5D5A),


    val lightGreen: Color = Color(0xFFAEF45C),
    val lightBlue: Color = Color(0xFFB7C6DE),
    val light: Color = Color(0xFFEEF6FF),
    val light2: Color = Color(0xFFF2F3F7),
    val error: Color = Color.Red,
    val transparent: Color = Color.Transparent,
)