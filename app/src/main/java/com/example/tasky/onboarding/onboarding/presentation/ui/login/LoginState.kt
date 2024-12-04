package com.example.tasky.onboarding.onboarding.presentation.ui.login

import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput

data class LoginState(
    val email: FieldInput = FieldInput(),
    val emailErrorStatus: ErrorStatus = ErrorStatus(false),
    val password: FieldInput = FieldInput(),
    val passwordErrorStatus: ErrorStatus = ErrorStatus(false),
    val isLoading: Boolean = false
)