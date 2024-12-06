package com.example.tasky.onboarding.onboarding.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.CredentialsTextField
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.presentation.components.ErrorDialog
import com.example.tasky.core.presentation.components.MainButton
import com.example.tasky.core.presentation.components.TaskyLoader
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.LoginViewModel
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography

@Composable
internal fun LoginScreen(
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val state by loginViewModel.state
    val dialogState by loginViewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        loginViewModel.navigationEvents.collect { event ->
            when (event) {
                LoginNavigationEvent.NavigateToRegister -> {
                    navController.navigate(Screen.Register) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }

                LoginNavigationEvent.NavigateToAgenda -> {
                    navController.navigate(Screen.Agenda)
                }
            }
        }
    }

    LoginContent(
        state = state,
        dialogState = dialogState,
        onAction = { loginViewModel.onAction(it) },
        )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginContent(
    state: LoginState,
    dialogState: DialogState,
    onAction: (LoginAction) -> Unit,
) {
    val context = LocalContext.current
    val cornerRadius = 30.dp

    if (dialogState is DialogState.Show) {
        ErrorDialog(
            title = stringResource(R.string.Something_went_wrong),
            label = dialogState.errorMessage?.asString(context).orEmpty(),
            displayCloseIcon = false,
            positiveButtonText = stringResource(R.string.OK),
            positiveOnClick = { onAction(LoginAction.OnDismissDialog) },
            onCancelClicked = { onAction(LoginAction.OnDismissDialog) }
        )
    }
    if (state.isLoading) {
        TaskyLoader(
            modifier = Modifier.zIndex(1f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.black)
    ) {
        Header()

        Surface(
            shape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius
            ),
            color = colors.white,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 150.dp)
        ) {
            MainContent(
                email = state.email,
                emailErrorStatus = state.emailErrorStatus,
                password = state.password,
                passwordErrorStatus = state.passwordErrorStatus,
                onAction = onAction
            )
            BottomText(onAction)

        }
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(colors.black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.Welcome_Back),
            style = typography.title,
            textAlign = TextAlign.Center,
            color = colors.white,
            modifier = Modifier.padding(bottom = dimensions.large32dp)
        )
    }
}

@Composable
private fun MainContent(
    email: FieldInput,
    emailErrorStatus: ErrorStatus,
    password: FieldInput,
    passwordErrorStatus: ErrorStatus,
    onAction: (LoginAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 50.dp)
            .padding(horizontal = dimensions.default16dp)
            .imePadding()
    ) {
        CredentialsTextField(
            modifier = Modifier.fillMaxWidth(),
            state = email,
            errorStatus = emailErrorStatus,
            placeholderValue = stringResource(R.string.Email_address),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            onValueChange = { onAction(LoginAction.OnEmailChange(it)) }
        )

        Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

        CredentialsTextField(
            modifier = Modifier.fillMaxWidth(),
            state = password,
            errorStatus = passwordErrorStatus,
            placeholderValue = stringResource(R.string.Password),
            isPasswordField = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            onValueChange = { onAction(LoginAction.OnPasswordChange(it)) }
        )

        Spacer(modifier = Modifier.height(dimensions.default16dp))

        MainButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(LoginAction.OnLoginClick)
            },
            btnString = stringResource(R.string.Log_in).uppercase(),
            textStyle = typography.buttonText
        )

    }
}

@Composable
private fun BottomText(
    onAction: (LoginAction) -> Unit
) {
    val styledText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = colors.gray)) {
            append(
                stringResource(R.string.Don_t_have_an_account)
            )
        }
        withStyle(
            style = SpanStyle(color = colors.lightBlue)
        ) {
            append(stringResource(R.string.Sign_up))
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = styledText,
            modifier = Modifier
                .padding(bottom = 70.dp)
                .clickable {
                    onAction(LoginAction.OnNavigateToRegister)
                },
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.W500,
                lineHeight = 30.sp
            )
        )
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginContent(
            state = LoginState(
                email = FieldInput("lori123@boohoo.com"),
                emailErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Please_enter_a_valid_email_address_error)
                ),
                password = FieldInput("boohoo123"),
                passwordErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Password_error)
                )
            ),
            dialogState = DialogState.Hide,
            onAction = { LoginAction.OnLoginClick },
        )
    }
}