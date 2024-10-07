package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.core.local.UserPrefsRepository
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.core.util.CredentialsValidator
import com.example.tasky.core.util.ErrorStatus
import com.example.tasky.core.util.FieldInput
import com.example.tasky.core.util.Result
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import com.example.tasky.onboarding.onboarding_domain.util.AuthError
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
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUiState>(LoginUiState.None)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    data class LoginState(
        val email: FieldInput = FieldInput(),
        val emailErrorStatus: ErrorStatus = ErrorStatus(false),
        val password: FieldInput = FieldInput(),
        val passwordErrorStatus: ErrorStatus = ErrorStatus(false)
    )

    fun login(email: FieldInput, password: FieldInput) {
        val emailErrorStatus = CredentialsValidator.validateEmail(email.value)
        val passwordErrorStatus = CredentialsValidator.validatePassword(password.value)

        _state.update {
            it.copy(
                email = email.copy(hasInteracted = true),
                emailErrorStatus = emailErrorStatus,
                password = password.copy(hasInteracted = true),
                passwordErrorStatus = passwordErrorStatus
            )
        }

        _uiState.update { LoginUiState.Loading }
        viewModelScope.launch {
            val result = defaultUserRepository.login(email.value, password.value)
            when (result) {
                is Result.Success -> {
                    Log.d("DDD", result.data.accessToken ?: "")
                    _uiState.update { LoginUiState.Success }
                    result.data.accessToken?.let { userPrefsRepository.updateAccessToken(it) }
                }

                is Result.Error -> {
                    val errorMessage = when (result.error) {
                        AuthError.Login.INVALID_CREDENTIALS -> "Check your credentials!"
                        AuthError.General.NO_INTERNET -> "No internet connection!"
                        else -> "Login failed!"
                    }
                    _uiState.update { LoginUiState.None }
                    _dialogState.update { DialogState.Show(errorMessage) }
                }
            }
        }

        _uiState.update { LoginUiState.None }
        _dialogState.update { DialogState.Show("Login failed!") }

    }

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
        data object Loading : LoginUiState()
        data object Success : LoginUiState()
    }

}