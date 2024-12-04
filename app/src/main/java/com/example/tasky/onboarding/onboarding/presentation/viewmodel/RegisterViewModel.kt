package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.onboarding.onboarding.presentation.ui.register.RegisterAction
import com.example.tasky.onboarding.onboarding.presentation.ui.register.RegisterNavigationEvent
import com.example.tasky.onboarding.onboarding.presentation.ui.register.RegisterState
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.util.CredentialsValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val defaultUserRepository: UserRepository,
) : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<RegisterNavigationEvent>()
    val navigationEvents: SharedFlow<RegisterNavigationEvent> = _navigationEvents

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnRegistrationClick -> {
                val state = _state.value
                register(state.fullName, state.email, state.password)
            }

            is RegisterAction.OnEmailChange -> handleEmailInput(action.email)
            is RegisterAction.OnNameChange -> handleNameInput(action.name)
            is RegisterAction.OnPasswordChange -> handlePasswordInput(action.password)
            RegisterAction.OnNavigateToLogin -> {
                viewModelScope.launch {
                    _navigationEvents.emit(RegisterNavigationEvent.NavigateToLogin)
                }
            }

            RegisterAction.OnDismissDialog -> hideDialog()
        }
    }

    private fun register(name: FieldInput, email: FieldInput, password: FieldInput) {
        val fullNameErrorStatus = CredentialsValidator.validateName(name.value)
        val emailErrorStatus = CredentialsValidator.validateEmail(email.value)
        val passwordErrorStatus = CredentialsValidator.validatePassword(password.value)
        var errorMessage: UiText

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
            _state.update { it.copy(isLoading = true) }

            viewModelScope.launch {
                val result = defaultUserRepository.register(name.value, email.value, password.value)
                when (result) {
                    is Result.Success -> {
//                        login(email.value, password.value)
                        _navigationEvents.emit(RegisterNavigationEvent.NavigateToLogin)
                    }

                    is Result.Error -> {
                        errorMessage = when (result.error) {
                            TaskyError.RegisterError.EMAIL_ALREADY_EXISTS -> UiText.StringResource(R.string.Email_already_in_use)
                            TaskyError.NetworkError.NO_INTERNET -> UiText.StringResource(R.string.No_internet_connection)
                            else -> UiText.StringResource(R.string.Registration_failed)
                        }
                        _dialogState.update { DialogState.Show(errorMessage) }
                    }
                }
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            defaultUserRepository.login(email, password)
        } catch (e: Exception) {
            _dialogState.update { DialogState.Show(UiText.StringResource(R.string.Login_failed)) }
        }
    }

    private fun handleNameInput(fullName: String) {
        val fullNameErrorStatus = CredentialsValidator.validateName(fullName)

        _state.update {
            it.copy(
                fullName = it.fullName.copy(
                    value = fullName,
                    hasInteracted = true
                ),
                fullNameErrorStatus = fullNameErrorStatus
            )
        }
    }

    private fun handleEmailInput(email: String) {
        val emailErrorStatus = CredentialsValidator.validateEmail(email)

        _state.update {
            it.copy(
                email = it.email.copy(
                    value = email,
                    hasInteracted = true
                ),
                emailErrorStatus = emailErrorStatus
            )
        }
    }

    private fun handlePasswordInput(password: String) {
        val passwordErrorStatus = CredentialsValidator.validatePassword(password)

        _state.update {
            it.copy(
                password = it.password.copy(
                    value = password,
                    hasInteracted = true
                ),
                passwordErrorStatus = passwordErrorStatus
            )
        }
    }

    private fun hideDialog() {
        _dialogState.update { DialogState.Hide }
    }

    private fun isFormValid(
        fullNameErrorStatus: ErrorStatus,
        emailErrorStatus: ErrorStatus,
        passwordErrorStatus: ErrorStatus
    ): Boolean {
        return !fullNameErrorStatus.hasError &&
                !emailErrorStatus.hasError &&
                !passwordErrorStatus.hasError
    }
}