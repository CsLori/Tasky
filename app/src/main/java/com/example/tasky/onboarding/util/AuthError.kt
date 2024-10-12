package com.example.tasky.onboarding.util

import com.example.tasky.core.domain.util.Error

sealed interface AuthError: Error {
    enum class Login : AuthError {
        INVALID_CREDENTIALS,
        ACCOUNT_LOCKED,
        TOO_MANY_ATTEMPTS
    }

    enum class Register : AuthError {
        EMAIL_ALREADY_EXISTS,
        INVALID_INPUT;
    }

    enum class General : AuthError {
        UNAUTHORIZED,
        TOKEN_EXPIRED,
        NO_INTERNET,
        SERVER_ERROR
    }
}