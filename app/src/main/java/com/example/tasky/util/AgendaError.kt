package com.example.tasky.util

sealed interface AgendaError : Error {
    enum class General : AgendaError {
        UNAUTHORIZED,
        NOT_FOUND,
        NO_INTERNET,
        SERVER_ERROR,
        GENERAL_ERROR
    }
}