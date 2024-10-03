package com.example.tasky.onboarding.onboarding.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.onboarding.onboarding_data.repository.UserRepositoryImpl
import com.example.tasky.util.CredentialsValidator
import com.example.tasky.util.FieldInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val credentialsValidator: CredentialsValidator
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.None)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.Hide)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    var nameField by mutableStateOf(FieldInput())
    val nameErrorStatus by derivedStateOf {
        credentialsValidator.validateName(nameField.value)
    }

    var emailField by mutableStateOf(FieldInput())
    val emailErrorStatus by derivedStateOf {
        credentialsValidator.validateEmail(emailField.value)
    }

    var passwordField by mutableStateOf(FieldInput())
    val passwordErrorStatus by derivedStateOf {
        credentialsValidator.validatePassword(passwordField.value)
    }

    fun register(name: String, email: String, password: String) {
        if (!isFormValid()) {
            return
        }
        _state.update { RegisterState.Loading }

        try {
            viewModelScope.launch {
                userRepositoryImpl.register(name, email, password)
                _state.update { RegisterState.Success }
            }

        } catch (e: Exception) {
            _dialogState.update { DialogState.Show("Something went wrong!") }
        }
    }

    fun onNameChange(newName: String) {
        nameField = nameField.copy(
            value = newName,
            hasInteracted = true
        )
    }

    fun onEmailChange(newEmail: String) {
        emailField = emailField.copy(
            value = newEmail,
            hasInteracted = true
        )
    }

    fun onPasswordChange(newPassword: String) {
        passwordField = passwordField.copy(
            value = newPassword,
            hasInteracted = true
        )
    }

    fun closeDialog() {
        _dialogState.value = DialogState.Hide
    }

    private fun isFormValid(): Boolean {
        return !nameErrorStatus.isError &&
                !emailErrorStatus.isError &&
                !passwordErrorStatus.isError
    }

    sealed class DialogState() {
        data object Hide : DialogState()
        data class Show(val errorMessage: String?) : DialogState()
    }

    sealed class RegisterState {
        data object None : RegisterState()
        data object Loading : RegisterState()
        data object Success : RegisterState()
    }

}