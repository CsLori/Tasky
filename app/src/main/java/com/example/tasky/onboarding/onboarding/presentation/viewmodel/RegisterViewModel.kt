package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class RegisterViewModel @Inject constructor(
    private val defaultUserRepository: DefaultUserRepository,
) : ViewModel() {

    private var _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.None)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun register(name: FieldInput, email: FieldInput, password: FieldInput) {
        val fullNameErrorStatus = CredentialsValidator.validateName(name.value)
        val emailErrorStatus = CredentialsValidator.validateEmail(email.value)
        val passwordErrorStatus = CredentialsValidator.validatePassword(password.value)

        _state.update {
            it.copy(
                fullName = name.copy(hasInteracted = true),
                fullNameErrorStatus = fullNameErrorStatus,
                email = email.copy(hasInteracted = true),
                emailErrorStatus = emailErrorStatus,
                password = password.copy(hasInteracted = true),
                passwordErrorStatus = passwordErrorStatus
            )
        }


        if (isFormValid(fullNameErrorStatus, emailErrorStatus, passwordErrorStatus)) {
            _uiState.update { RegisterUiState.Loading }

            viewModelScope.launch {
                val result = defaultUserRepository.register(name.value, email.value, password.value)
                when (result) {
                    is Result.Success -> {
//                        login(email.value, password.value)
//                        _uiState.update { RegisterUiState.Success }
                    }

                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            AuthError.Register.EMAIL_ALREADY_EXISTS -> "Email is already in use!"
                            AuthError.General.NO_INTERNET -> "No internet connection!"
                            else -> "Registration failed!"
                        }
                        _uiState.update { RegisterUiState.None }
                        _dialogState.update { DialogState.Show(errorMessage) }
                    }
                }
            }
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            defaultUserRepository.login(email, password)
            _uiState.update { RegisterUiState.Success }
        } catch (e: Exception) {
            _dialogState.update { DialogState.Show("Login failed!") }
        }
    }

    fun onNameChange(fullNameInput: String) {
        val fullNameErrorStatus = CredentialsValidator.validateName(fullNameInput)

        _state.update {
            it.copy(
                fullName = it.fullName.copy(
                    value = fullNameInput,
                    hasInteracted = true
                ),
                fullNameErrorStatus = fullNameErrorStatus
            )
        }
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

    fun closeDialog() {
        _dialogState.value = DialogState.Hide
    }

    private fun isFormValid(
        fullNameErrorStatus: ErrorStatus,
        emailErrorStatus: ErrorStatus,
        passwordErrorStatus: ErrorStatus
    ): Boolean {
        return !fullNameErrorStatus.isError &&
                !emailErrorStatus.isError &&
                !passwordErrorStatus.isError
    }

    data class RegisterState(
        val fullName: FieldInput = FieldInput(),
        val fullNameErrorStatus: ErrorStatus = ErrorStatus(false),
        val email: FieldInput = FieldInput(),
        val emailErrorStatus: ErrorStatus = ErrorStatus(false),
        val password: FieldInput = FieldInput(),
        val passwordErrorStatus: ErrorStatus = ErrorStatus(false)
    )

    sealed interface RegisterAction {
        data class OnNameChange(val name: String) : RegisterAction
        data class OnEmailChange(val email: String) : RegisterAction
        data class OnPasswordChange(val password: String) : RegisterAction
        data object OnRegistrationClick : RegisterAction
        data object OnNavigateToLogin : RegisterAction
        data object OnDismissDialog : RegisterAction
    }

    sealed class RegisterUiState {
        data object None : RegisterUiState()
        data object Loading : RegisterUiState()
        data object Success : RegisterUiState()
    }
}