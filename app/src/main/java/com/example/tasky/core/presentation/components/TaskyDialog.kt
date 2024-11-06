package com.example.tasky.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasky.R
import com.example.tasky.agenda.agenda_presentation.viewmodel.state.AgendaDetailStateUpdate
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.ui.theme.AppTheme

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

@Composable
fun AddVisitorDialog(
    title: String? = null,
    displayCloseIcon: Boolean = false,
    positiveButtonText: String,
    onPositiveClick: () -> Unit,
    emailErrorStatus: ErrorStatus,
    onCancelClicked: (() -> Unit)? = null,
    email: FieldInput,
    onUpdateState: (AgendaDetailStateUpdate) -> Unit,
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (displayCloseIcon) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
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

                title?.let {
                    Text(
                        modifier = Modifier.padding(bottom = 18.dp),
                        text = it,
                        style = AppTheme.typography.title.copy(fontSize = 20.sp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.dimensions.default16dp))

                CredentialsTextField(
                    modifier = Modifier.fillMaxWidth(),
                    fieldInput = email,
                    errorStatus = emailErrorStatus,
                    placeholderValue = stringResource(R.string.Email_address),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = { email ->
                        onUpdateState(
                            AgendaDetailStateUpdate.UpdateAddVisitorEmail(
                                FieldInput(email).copy(
                                    hasInteracted = true
                                )
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(AppTheme.dimensions.default16dp))

                DialogSuccessButton(
                    modifier = Modifier.fillMaxWidth(),
                    btnString = positiveButtonText.uppercase(),
                    onClick = { onPositiveClick() },
                    textStyle = AppTheme.typography.buttonText
                )
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

@Preview
@Composable
fun AddVisitorDialogPreview() {
    AppTheme {
        AddVisitorDialog(
            title = "Add visitor",
            displayCloseIcon = true,
            positiveButtonText = "Add",
            onPositiveClick = {},
            onCancelClicked = {},
            email = FieldInput("lori123@boohoo.com"),
            emailErrorStatus = ErrorStatus(false),
            onUpdateState = {},
        )
    }
}