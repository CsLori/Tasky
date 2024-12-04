package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.onboarding.onboarding.presentation.ui.login.LoginAction
import com.example.tasky.onboarding.onboarding.presentation.ui.login.LoginNavigationEvent
import com.example.tasky.onboarding.onboarding.presentation.ui.login.LoginState
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.onboarding.onboarding_domain.model.LoginUser
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
class LoginViewModel @Inject constructor(
    private val defaultUserRepository: UserRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvents: SharedFlow<LoginNavigationEvent> = _navigationEvents

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sessionState = MutableStateFlow<SessionState?>(null)
    val sessionState: StateFlow<SessionState?> = _sessionState.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    init {
        increaseSessionCount()
//        login(FieldInput("lori123@boohoo.com"),FieldInput("Orlando123") )
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnNavigateToRegister -> {
                viewModelScope.launch {
                    _navigationEvents.emit(LoginNavigationEvent.NavigateToRegister)
                }
            }

            LoginAction.OnDismissDialog -> onDismissDialog()

            is LoginAction.OnEmailChange -> onEmailChange(action.email)

            is LoginAction.OnPasswordChange -> onPasswordChange(action.password)

            LoginAction.OnLoginClick -> login(_state.value.email, _state.value.password)

            LoginAction.OnNavigateToAgenda -> {
                viewModelScope.launch {
                    _navigationEvents.emit(LoginNavigationEvent.NavigateToAgenda)
                }
            }
        }
    }

    fun login(email: FieldInput, password: FieldInput) {
        val emailErrorStatus = CredentialsValidator.validateEmail(email.value)
        val passwordErrorStatus = CredentialsValidator.validatePassword(password.value)
        var errorMessage: UiText

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
                when (val result = defaultUserRepository.login(email.value, password.value)) {
                    is Result.Success -> {
                        // Update auth related tokens
                        updateTokens(result.data, email.value)

                        // We don't want to bother the user more than 3 times
                        if (getSessionCount() < 4) {
                            increaseSessionCount()
                            setHasSeenNotificationPrompt(false)
                        }

                        // Get full agenda for local db sync
                        agendaRepository.getFullAgenda()
                        _navigationEvents.emit(LoginNavigationEvent.NavigateToAgenda)
                    }

                    is Result.Error -> {
                        errorMessage = when (result.error) {
                            TaskyError.LoginError.INVALID_CREDENTIALS -> UiText.StringResource(R.string.Check_your_credentials)
                            TaskyError.NetworkError.NO_INTERNET -> UiText.StringResource(R.string.No_internet_connection)
                            else -> UiText.StringResource(R.string.Login_failed)
                        }
                        _dialogState.update { DialogState.Show(errorMessage) }
                    }
                }
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun checkUserSession() {
        viewModelScope.launch {
            val result = defaultUserRepository.authenticate()
            when (result) {
                is Result.Success -> _sessionState.update { SessionState.Valid }
                is Result.Error -> _sessionState.update { SessionState.Invalid }
            }
        }
    }

    private suspend fun updateTokens(loginUser: LoginUser, email: String) {
        loginUser.apply {
            userPrefsRepository.updateRefreshToken(refreshToken)
            userPrefsRepository.updateAccessToken(accessToken)
            userPrefsRepository.updateUserId(userId)
            userPrefsRepository.updateUserName(fullName)
            userPrefsRepository.updateAccessTokenExpirationTimestamp(accessTokenExpirationTimestamp)
            userPrefsRepository.updateUserEmail(email)

        }
    }

    private fun onDismissDialog() {
        _dialogState.value = DialogState.Hide
    }

    private fun isFormValid(emailErrorStatus: ErrorStatus, passwordErrorStatus: ErrorStatus) =
        !emailErrorStatus.hasError && !passwordErrorStatus.hasError

    private fun onEmailChange(emailInput: String) {
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

    private fun onPasswordChange(passwordInput: String) {
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

    private fun increaseSessionCount() {
        viewModelScope.launch {
            userPrefsRepository.updateSessionCount(userPrefsRepository.getSessionCount() + 1)
        }
    }

    private fun getSessionCount(): Int {
        viewModelScope.launch {
            _sessionCount.value = userPrefsRepository.getSessionCount()
        }
        return sessionCount.value
    }

    private fun setHasSeenNotificationPrompt(hasSeenNotificationPrompt: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.updateHasSeenNotificationPrompt(hasSeenNotificationPrompt)
        }
    }

    sealed class SessionState {
        data object Valid : SessionState()
        data object Invalid : SessionState()
    }
}