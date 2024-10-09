package com.example.tasky.agenda.agenda_domain.util

import com.example.tasky.core.domain.util.Error

sealed interface AgendaError : Error {
    enum class General : AgendaError {
        UNAUTHORIZED,
        NOT_FOUND,
        NO_INTERNET,
        SERVER_ERROR,
        GENERAL_ERROR
    }
}