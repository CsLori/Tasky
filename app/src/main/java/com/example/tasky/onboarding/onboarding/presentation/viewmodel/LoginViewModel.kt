package com.example.tasky.onboarding.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput
import com.example.tasky.core.presentation.UiText
import com.example.tasky.core.presentation.components.DialogState
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import com.example.tasky.onboarding.onboarding_domain.model.LoginUser
import com.example.tasky.util.CredentialsValidator
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
    private val userPrefsRepository: ProtoUserPrefsRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUiState>(LoginUiState.None)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private var _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sessionState = MutableStateFlow<SessionState?>(null)
    val sessionState: StateFlow<SessionState?> = _sessionState.asStateFlow()

    init {
        login(FieldInput("lori123@boohoo.com"),FieldInput("Orlando123") )
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
                val result = defaultUserRepository.login(email.value, password.value)
                when (result) {
                    is Result.Success -> {
                        _uiState.update { LoginUiState.Success }
                        // Update auth related tokens
                        updateTokens(result, email.value)

                        // Get full agenda for local b sync
//                        agendaRepository.getFullAgenda()
                    }

                    is Result.Error -> {
                        errorMessage = when (result.error) {
                            TaskyError.LoginError.INVALID_CREDENTIALS -> UiText.StringResource(R.string.Check_your_credentials)
                            TaskyError.NetworkError.NO_INTERNET -> UiText.StringResource(R.string.No_internet_connection)
                            else -> UiText.StringResource(R.string.Login_failed)
                        }
                        _uiState.update { LoginUiState.None }
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

    private suspend fun updateTokens(result: Result.Success<LoginUser>, email: String) {
        result.data.apply {
            userPrefsRepository.updateRefreshToken(refreshToken)
            userPrefsRepository.updateAccessToken(accessToken)
            userPrefsRepository.updateUserId(userId)
            userPrefsRepository.updateUserName(fullName)
            userPrefsRepository.updateAccessTokenExpirationTimestamp(accessTokenExpirationTimestamp)
            userPrefsRepository.updateUserEmail(email)

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

    sealed class SessionState {
        object Valid : SessionState()
        object Invalid : SessionState()
    }

    sealed class LoginUiState {
        data object None : LoginUiState()
        data object Success : LoginUiState()
    }

}