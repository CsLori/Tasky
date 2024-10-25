package com.example.tasky.core.domain

import retrofit2.HttpException
import java.io.IOException

fun mapToTaskyError(e: Exception): TaskyError {
    return when (e) {
        is HttpException -> when (e.code()) {
            401 -> TaskyError.LoginError.INVALID_CREDENTIALS
            403 -> TaskyError.AuthenticationError.NO_ACCESS
            404 -> TaskyError.NetworkError.NOT_FOUND
            409 -> TaskyError.NetworkError.SERVER_ERROR
            else -> TaskyError.NetworkError.GENERAL_ERROR
        }
        is IOException -> TaskyError.NetworkError.NO_INTERNET
        else -> TaskyError.NetworkError.SERVER_ERROR
    }
}