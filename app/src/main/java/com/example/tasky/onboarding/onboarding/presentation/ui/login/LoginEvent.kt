package com.example.tasky.onboarding.onboarding.presentation.ui.login

sealed class LoginNavigationEvent {
    data object NavigateToRegister : LoginNavigationEvent()
    data object NavigateToAgenda : LoginNavigationEvent()
}