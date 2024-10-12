package com.example.tasky.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    btnString: String,
    textStyle: TextStyle,
    shape: Shape? = null
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.black,
            contentColor = colors.white
        ),
        onClick = onClick
    ) {
        Text(
            btnString,
            modifier = Modifier.padding(dimensions.default16dp),
            style = textStyle
        )
    }
}

@Composable
fun DialogSuccessButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    btnString: String,
    textStyle: TextStyle
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.black,
            contentColor = colors.white
        ),
        onClick = onClick
    ) {
        Text(
            btnString,
            modifier = Modifier.padding(dimensions.small8dp),
            style = textStyle
        )
    }
}

@Preview
@Composable
fun BaseButtonPreview() {
    AppTheme {
        MainButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            btnString = "Log in",
            textStyle = AppTheme.typography.buttonText
        )
    }
}