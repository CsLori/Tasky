package com.example.tasky.onboarding.onboarding.presentation.ui.register

sealed interface RegisterAction {
    data class OnNameChange(val name: String) : RegisterAction
    data class OnEmailChange(val email: String) : RegisterAction
    data class OnPasswordChange(val password: String) : RegisterAction
    data object OnRegistrationClick : RegisterAction
    data object OnNavigateToLogin : RegisterAction
    data object OnDismissDialog : RegisterAction
}