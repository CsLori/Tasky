package com.example.tasky.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class AppTypography(
    val title: TextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 30.sp
    ),

    val textFieldString: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 30.sp
    ),

    val buttonText: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700,
        lineHeight = 30.sp
    ),

    //General text styles
    val bodyNormal: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    val bodySemiBold: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    val bodyBold: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),

    val smallBodyNormal: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val smallBodyBold: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),

    //Button text styles
    val primaryButtonText: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    val secondaryButtonText: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    val smallButtonText: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
)