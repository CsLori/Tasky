package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.util.CredentialsValidator
import com.example.tasky.core.util.ErrorStatus
import com.example.tasky.core.util.FieldInput
import com.example.tasky.core.util.Result
import com.example.tasky.core.util.UiText
import com.example.tasky.onboarding.onboarding_data.remote.LoginResponse
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import com.example.tasky.onboarding.util.AuthError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val defaultUserRepository: DefaultUserRepository,
    private val userPrefsRepository: ProtoUserPrefsRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUiState>(LoginUiState.None)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: FieldInput, password: FieldInput) {
        val emailErrorStatus = CredentialsValidator.validateEmail(email.value)
        val passwordErrorStatus = CredentialsValidator.validatePassword(password.value)
        var errorMessage: String

        _state.update {
            it.copy(
                email = email.copy(hasInteracted = true),
                emailErrorStatus = emailErrorStatus,
                password = password.copy(hasInteracted = true),
                passwordErrorStatus = passwordErrorStatus
            )
        }

        if (isFormValid(emailErrorStatus, passwordErrorStatus)) {
            _state.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                val result = defaultUserRepository.login(email.value, password.value)
                when (result) {
                    is Result.Success -> {
                        _uiState.update { LoginUiState.Success }
                        // Update auth related tokens
                        updateTokens(result)
                    }

                    is Result.Error -> {
                        errorMessage = when (result.error) {
                            AuthError.Login.INVALID_CREDENTIALS -> UiText.StringResource(R.string.Check_your_credentials)
                                .toString()

                            AuthError.General.NO_INTERNET -> UiText.StringResource(R.string.No_internet_connection)
                                .toString()

                            else -> UiText.StringResource(R.string.Login_failed).toString()
                        }
                        _uiState.update { LoginUiState.None }
                        _dialogState.update { DialogState.Show(errorMessage) }
                    }
                }
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun updateTokens(result: Result.Success<LoginResponse>) {
        result.data.apply {
            refreshToken?.let { userPrefsRepository.updateRefreshToken(it) }
            accessToken?.let { userPrefsRepository.updateAccessToken(it) }
            userId?.let { userPrefsRepository.updateUserId(it) }
        }
    }

    fun onDismissDialog() {
        _dialogState.value = DialogState.Hide
    }

    private fun isFormValid(emailErrorStatus: ErrorStatus, passwordErrorStatus: ErrorStatus) =
        !emailErrorStatus.hasError && !passwordErrorStatus.hasError

    fun onEmailChange(emailInput: String) {
        val emailErrorStatus = CredentialsValidator.validateEmail(emailInput)

        _state.update {
            it.copy(
                email = it.email.copy(
                    value = emailInput,
                    hasInteracted = true
                ),
                emailErrorStatus = emailErrorStatus
            )
        }
    }

    fun onPasswordChange(passwordInput: String) {
        val passwordErrorStatus = CredentialsValidator.validatePassword(passwordInput)

        _state.update {
            it.copy(
                password = it.password.copy(
                    value = passwordInput,
                    hasInteracted = true
                ),
                passwordErrorStatus = passwordErrorStatus
            )
        }
    }

    data class LoginState(
        val email: FieldInput = FieldInput(),
        val emailErrorStatus: ErrorStatus = ErrorStatus(false),
        val password: FieldInput = FieldInput(),
        val passwordErrorStatus: ErrorStatus = ErrorStatus(false),
        val isLoading: Boolean = false
    )

    sealed interface LoginAction {
        data class OnEmailChange(val email: String) : LoginAction
        data class OnPasswordChange(val password: String) : LoginAction
        data object OnLoginClick : LoginAction
        data object OnNavigateToRegister : LoginAction
        data object OnNavigateToAgenda : LoginAction
        data object OnDismissDialog : LoginAction
    }

    sealed class LoginUiState {
        data object None : LoginUiState()
        data object Success : LoginUiState()
    }

}