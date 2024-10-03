package com.example.tasky.onboarding.onboarding.presentation

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.components.BaseButton
import com.example.tasky.components.BaseDialog
import com.example.tasky.components.BaseTextField
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.ErrorStatus
import com.example.tasky.util.FieldInput
import com.example.tasky.util.UiText
import dagger.hilt.android.AndroidEntryPoint

@Composable
internal fun RegisterScreen(registerViewModel: RegisterViewModel, navController: NavController) {
    val state by registerViewModel.state.collectAsStateWithLifecycle()
    val dialogState by registerViewModel.dialogState.collectAsStateWithLifecycle()
    val nameField = registerViewModel.nameField
    val emailField = registerViewModel.emailField
    val passwordField = registerViewModel.passwordField
    val nameErrorStatus = registerViewModel.nameErrorStatus
    val emailErrorStatus = registerViewModel.emailErrorStatus
    val passwordErrorStatus = registerViewModel.passwordErrorStatus

    RegisterContent(
        state = state,
        dialogState = dialogState,
        nameField = nameField,
        emailField = emailField,
        passwordField = passwordField,
        nameErrorStatus = nameErrorStatus,
        emailErrorStatus = emailErrorStatus,
        passwordErrorStatus = passwordErrorStatus,
        onRegisterClick = { name, email, password ->
            registerViewModel.register(name, email, password)
        },
        onNavigateToLogin = { navController.navigate(Screen.Login) },
        onDismiss = registerViewModel::closeDialog,
        onNameChange = { registerViewModel.onNameChange(it) },
        onEmailChange = { registerViewModel.onEmailChange(it) },
        onPasswordChange = { registerViewModel.onPasswordChange(it) },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterContent(
    state: RegisterViewModel.RegisterState,
    dialogState: RegisterViewModel.DialogState,
    nameField: FieldInput,
    emailField: FieldInput,
    passwordField: FieldInput,
    nameErrorStatus: ErrorStatus,
    emailErrorStatus: ErrorStatus,
    passwordErrorStatus: ErrorStatus,
    onRegisterClick: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var email by remember { mutableStateOf(("")) }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }


    when (state) {
        RegisterViewModel.RegisterState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        RegisterViewModel.RegisterState.Success -> {
            Log.d("DDD", "Success!")
            TODO()
        }

        RegisterViewModel.RegisterState.None -> {
            if (dialogState is RegisterViewModel.DialogState.Show) {
                BaseDialog(
                    title = "Something went wrong!",
                    label = "Something",
                    displayCloseIcon = true,
                    positiveButtonText = "Ok",
                    positiveOnClick = onDismiss,
                    onCancelClicked = onDismiss,
                )
            }
            Scaffold(floatingActionButton = {
                FloatingActionButton(
                    containerColor = colors.black,
                    onClick = { onNavigateToLogin() },
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
                            BaseTextField(
                                modifier = Modifier.fillMaxWidth(),
                                fieldInput = nameField,
                                placeholderValue = stringResource(R.string.Name),
                                errorStatus = nameErrorStatus,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    name = it
                                    onNameChange(name)
                                }
                            )

                            Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

                            BaseTextField(
                                modifier = Modifier.fillMaxWidth(),
                                fieldInput = emailField,
                                placeholderValue = stringResource(R.string.Email_address),
                                errorStatus = emailErrorStatus,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    email = it
                                    onEmailChange(email)
                                }
                            )

                            Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

                            BaseTextField(
                                modifier = Modifier.fillMaxWidth(),
                                fieldInput = passwordField,
                                errorStatus = passwordErrorStatus,
                                placeholderValue = stringResource(R.string.Password),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                isPasswordField = true,
                                onValueChange = {
                                    password = it
                                    onPasswordChange(password)
                                }
                            )

                            Spacer(modifier = Modifier.height(dimensions.extraLarge64dp))

                            BaseButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    onRegisterClick(
                                        nameField.value,
                                        emailField.value,
                                        passwordField.value
                                    )
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


@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        RegisterContent(
            state = RegisterViewModel.RegisterState.None,
            dialogState = RegisterViewModel.DialogState.Hide,
            onRegisterClick = { _, _, _ -> {} },
            onNavigateToLogin = {},
            onDismiss = {},
            nameField = FieldInput("L"),
            emailField = FieldInput("csakjhajfhas"),
            passwordField = FieldInput("jhjk"),
            nameErrorStatus = ErrorStatus(true, UiText.StringResource(R.string.Name_length_error)),
            emailErrorStatus = ErrorStatus(
                true,
                UiText.StringResource(R.string.Please_enter_a_valid_email_address_error)
            ),
            passwordErrorStatus = ErrorStatus(
                true,
                UiText.StringResource(R.string.Please_enter_a_valid_email_address_error)
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {}
        )
    }
}