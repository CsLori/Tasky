package com.example.tasky.onboarding.onboarding.presentation.ui.register

sealed interface RegisterNavigationEvent {
    data object NavigateToLogin : RegisterNavigationEvent
}