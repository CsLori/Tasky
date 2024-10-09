package com.example.tasky.core.util

data class ErrorStatus(
    val hasError: Boolean,
    val errorMsg: UiText? = null,
)