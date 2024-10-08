package com.example.tasky.onboarding.onboarding.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.core.presentation.components.CredentialsTextField
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.presentation.components.MainButton
import com.example.tasky.core.presentation.components.SuccessDialog
import com.example.tasky.core.util.ErrorStatus
import com.example.tasky.core.util.FieldInput
import com.example.tasky.core.util.UiText
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.RegisterViewModel
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography

@Composable
internal fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit
) {
    val state by registerViewModel.state.collectAsStateWithLifecycle()
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()
    val dialogState by registerViewModel.dialogState.collectAsStateWithLifecycle()

    RegisterContent(
        state = state,
        uiState = uiState,
        dialogState = dialogState,
        onAction = { action ->
            when (action) {
                RegisterViewModel.RegisterAction.OnRegistrationClick -> {
                    registerViewModel.register(state.fullName, state.email, state.password)
                }

                is RegisterViewModel.RegisterAction.OnEmailChange -> registerViewModel.onEmailChange(
                    action.email
                )

                is RegisterViewModel.RegisterAction.OnNameChange -> registerViewModel.onNameChange(
                    action.name
                )

                is RegisterViewModel.RegisterAction.OnPasswordChange -> registerViewModel.onPasswordChange(
                    action.password
                )

                RegisterViewModel.RegisterAction.OnNavigateToLogin -> {
                    onNavigateToLogin()
                }

                RegisterViewModel.RegisterAction.OnDismissDialog -> {
                    registerViewModel.closeDialog()
                }

            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RegisterContent(
    state: RegisterViewModel.RegisterState,
    uiState: RegisterViewModel.RegisterUiState,
    dialogState: DialogState,
    onAction: (RegisterViewModel.RegisterAction) -> Unit
) {

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        when (uiState) {

            RegisterViewModel.RegisterUiState.Success -> {
                Log.d("DDD", "Success!")
                RegisterViewModel.RegisterAction.OnNavigateToLogin
            }

            RegisterViewModel.RegisterUiState.None -> {
                if (dialogState is DialogState.Show) {
                    SuccessDialog(
                        title = "Something went wrong!",
                        label = "Something",
                        displayCloseIcon = true,
                        positiveButtonText = "Ok",
                        positiveOnClick = { onAction(RegisterViewModel.RegisterAction.OnDismissDialog) },
                        onCancelClicked = { onAction(RegisterViewModel.RegisterAction.OnDismissDialog) },
                    )
                }
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        containerColor = colors.black,
                        onClick = { onAction(RegisterViewModel.RegisterAction.OnNavigateToLogin) },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back Arrow",
                            tint = colors.white,
                        )
                    }
                }, floatingActionButtonPosition = FabPosition.Start) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(colors.black),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Text(
                                text = stringResource(R.string.Create_your_account),
                                style = typography.title,
                                textAlign = TextAlign.Center,
                                color = colors.white,
                                modifier = Modifier.padding(bottom = dimensions.large32dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp
                            ),
                            color = colors.white,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(top = 150.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(top = 40.dp)
                                    .padding(horizontal = dimensions.default16dp)
                            ) {
                                CredentialsTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    fieldInput = state.fullName,
                                    placeholderValue = stringResource(R.string.Name),
                                    errorStatus = state.fullNameErrorStatus,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    onValueChange = {
                                        onAction(
                                            RegisterViewModel.RegisterAction.OnNameChange(it)
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

                                CredentialsTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    fieldInput = state.email,
                                    placeholderValue = stringResource(R.string.Email_address),
                                    errorStatus = state.emailErrorStatus,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    onValueChange = {
                                        onAction(
                                            RegisterViewModel.RegisterAction.OnEmailChange(it)
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

                                CredentialsTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    fieldInput = state.password,
                                    errorStatus = state.passwordErrorStatus,
                                    placeholderValue = stringResource(R.string.Password),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                    ),
                                    isPasswordField = true,
                                    onValueChange = {
                                        onAction(
                                            RegisterViewModel.RegisterAction.OnPasswordChange(
                                                it
                                            )
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(dimensions.extraLarge64dp))

                                MainButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        onAction(RegisterViewModel.RegisterAction.OnRegistrationClick)
                                    },
                                    btnString = stringResource(R.string.Get_Started).uppercase(),
                                    textStyle = typography.buttonText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        RegisterContent(
            state = RegisterViewModel.RegisterState(
                fullName = FieldInput("Lorant Csuhai"),
                email = FieldInput("lori123@boohoo.com"),
                password = FieldInput("boohoo123"),
                fullNameErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Name_length_error)
                )
            ),
            uiState = RegisterViewModel.RegisterUiState.None,
            dialogState = DialogState.Hide,
            onAction = {}
        )
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun RegisterScreenWithErrorDialogPreview() {
    AppTheme {
        RegisterContent(
            state = RegisterViewModel.RegisterState(
                fullName = FieldInput("Lorant Csuhai"),
                email = FieldInput("lori123@boohoo.com"),
                password = FieldInput("boohoo123"),
                fullNameErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Name_length_error)
                )
            ),
            uiState = RegisterViewModel.RegisterUiState.None,
            dialogState = DialogState.Show("Some Error!"),
            onAction = {}
        )
    }
}