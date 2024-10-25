package com.example.tasky.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.core.presentation.UiText

@ExperimentalComposeUiApi
@Composable
fun ErrorDialog(
    title: String? = null,
    label: String,
    displayCloseIcon: Boolean = false,
    positiveButtonText: String,
    positiveOnClick: () -> Unit,
    onCancelClicked: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = { onCancelClicked?.invoke() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimensions.default16dp),
            color = AppTheme.colors.white
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title?.uppercase()?.let {
                        Text(
                            modifier = Modifier.padding(bottom = 18.dp),
                            text = it,
                            style = AppTheme.typography.bodyMedium
                        )
                    }

                    if (displayCloseIcon) {
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onCancelClicked?.invoke() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = AppTheme.colors.black
                            )
                        }
                    }
                }
                Text(
                    text = label,
                    style = AppTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(AppTheme.dimensions.default16dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DialogSuccessButton(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        btnString = positiveButtonText.uppercase(),
                        onClick = { positiveOnClick() },
                        textStyle = AppTheme.typography.buttonText
                    )
                }
            }
        }
    }
}

sealed class DialogState {
    data object Hide : DialogState()
    data class Show(val errorMessage: UiText?) : DialogState()
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun DialogPreview() {
    AppTheme {
        ErrorDialog(
            title = "Title", label = "Label",
            displayCloseIcon = false,
            positiveButtonText = "Ok",
            positiveOnClick = {},
            onCancelClicked = {},
        )
    }
}