package com.example.tasky.onboarding.onboarding.presentation.ui.register

import com.example.tasky.core.presentation.ErrorStatus
import com.example.tasky.core.presentation.FieldInput

data class RegisterState(
    val fullName: FieldInput = FieldInput(),
    val email: FieldInput = FieldInput(),
    val password: FieldInput = FieldInput(),
    val fullNameErrorStatus: ErrorStatus = ErrorStatus(false),
    val emailErrorStatus: ErrorStatus = ErrorStatus(false),
    val passwordErrorStatus: ErrorStatus = ErrorStatus(false),
    val isLoading: Boolean = false
)