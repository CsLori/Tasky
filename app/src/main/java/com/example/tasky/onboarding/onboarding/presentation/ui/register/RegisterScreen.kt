package com.example.tasky.onboarding.onboarding.presentation.ui.register

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.CredentialsTextField
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.presentation.components.ErrorDialog
import com.example.tasky.core.presentation.components.MainButton
import com.example.tasky.core.presentation.components.TaskyLoader
import com.example.tasky.onboarding.onboarding.presentation.viewmodel.RegisterViewModel
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography

@Composable
internal fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val state by registerViewModel.state.collectAsStateWithLifecycle()
    val dialogState by registerViewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        registerViewModel.navigationEvents.collect { event ->
            when (event) {
                RegisterNavigationEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    RegisterContent(
        state = state,
        dialogState = dialogState,
        onAction = { action -> registerViewModel.onAction(action) }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RegisterContent(
    state: RegisterState,
    dialogState: DialogState,
    onAction: (RegisterAction) -> Unit
) {
    val context = LocalContext.current

    if (state.isLoading) {
        TaskyLoader()
    }

    if (dialogState is DialogState.Show) {
        ErrorDialog(
            title = stringResource(R.string.Something_went_wrong),
            label = dialogState.errorMessage?.asString(context) ?: "",
            displayCloseIcon = true,
            positiveButtonText = stringResource(R.string.OK),
            positiveOnClick = { onAction(RegisterAction.OnDismissDialog) },
            onCancelClicked = { onAction(RegisterAction.OnDismissDialog) },
        )
    }
    Scaffold(
        floatingActionButton = {
            GoBackFab(onAction)
        }, floatingActionButtonPosition = FabPosition.Start
    ) { innerPadding ->
        val cornerRadius = 30.dp
        Box(
            modifier = Modifier
                .fillMaxSize(),
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
                    fullName = state.fullName,
                    fullNameErrorStatus = state.fullNameErrorStatus,
                    email = state.email,
                    emailErrorStatus = state.emailErrorStatus,
                    password = state.password,
                    passwordErrorStatus = state.passwordErrorStatus,
                    onAction = onAction
                )
            }
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
            text = stringResource(R.string.Create_your_account),
            style = typography.title,
            textAlign = TextAlign.Center,
            color = colors.white,
            modifier = Modifier.padding(bottom = dimensions.large32dp)
        )
    }
}

@Composable
private fun MainContent(
    fullName: FieldInput,
    fullNameErrorStatus: ErrorStatus,
    email: FieldInput,
    emailErrorStatus: ErrorStatus,
    password: FieldInput,
    passwordErrorStatus: ErrorStatus,
    onAction: (RegisterAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .padding(horizontal = dimensions.default16dp)
            .imePadding()
    ) {
        CredentialsTextField(
            modifier = Modifier.fillMaxWidth(),
            fieldInput = fullName,
            placeholderValue = stringResource(R.string.Name),
            errorStatus = fullNameErrorStatus,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            onValueChange = { onAction(RegisterAction.OnNameChange(it)) }
        )

        Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

        CredentialsTextField(
            modifier = Modifier.fillMaxWidth(),
            fieldInput = email,
            placeholderValue = stringResource(R.string.Email_address),
            errorStatus = emailErrorStatus,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            onValueChange = { onAction(RegisterAction.OnEmailChange(it)) }
        )

        Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

        CredentialsTextField(
            modifier = Modifier.fillMaxWidth(),
            fieldInput = password,
            errorStatus = passwordErrorStatus,
            placeholderValue = stringResource(R.string.Password),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isPasswordField = true,
            onValueChange = { onAction(RegisterAction.OnPasswordChange(it)) }
        )

        Spacer(modifier = Modifier.height(dimensions.extraLarge64dp))

        MainButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(RegisterAction.OnRegistrationClick) },
            btnString = stringResource(R.string.Get_Started).uppercase(),
            textStyle = typography.buttonText
        )
    }
}

@Composable
private fun GoBackFab(onAction: (RegisterAction) -> Unit) {
    FloatingActionButton(
        containerColor = colors.black,
        onClick = { onAction(RegisterAction.OnNavigateToLogin) },
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = "Back Arrow",
            tint = colors.white,
        )
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        RegisterContent(
            state = RegisterState(
                fullName = FieldInput("Lorant Csuhai"),
                email = FieldInput("lori123@boohoo.com"),
                password = FieldInput("boohoo123"),
                fullNameErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Name_length_error)
                )
            ),
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
            state = RegisterState(
                fullName = FieldInput("Lorant Csuhai"),
                email = FieldInput("lori123@boohoo.com"),
                password = FieldInput("boohoo123"),
                fullNameErrorStatus = ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Name_length_error)
                )
            ),
            dialogState = DialogState.Show(UiText.StringResource(R.string.Registration_failed)),
            onAction = {}
        )
    }
}