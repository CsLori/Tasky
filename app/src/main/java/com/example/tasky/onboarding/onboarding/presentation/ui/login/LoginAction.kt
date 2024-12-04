package com.example.tasky.onboarding.onboarding.presentation.ui.login

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnLoginClick : LoginAction
    data object OnNavigateToRegister : LoginAction
    data object OnNavigateToAgenda : LoginAction
    data object OnDismissDialog : LoginAction
}