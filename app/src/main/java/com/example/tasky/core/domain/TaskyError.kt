package com.example.tasky.core.domain

sealed interface TaskyError : Error {
    enum class LoginError : TaskyError {
        INVALID_CREDENTIALS,
        TOO_MANY_ATTEMPTS
    }

    enum class RegisterError : TaskyError {
        EMAIL_ALREADY_EXISTS,
        INVALID_INPUT;
    }

    enum class NetworkError : TaskyError {
        NO_INTERNET,
        SERVER_ERROR,
        NOT_FOUND,
        GENERAL_ERROR,
        IMAGE_TOO_LARGE;
    }

    enum class AuthenticationError : TaskyError {
        UNAUTHORIZED,
        TOKEN_EXPIRED,
        NO_ACCESS
    }
}