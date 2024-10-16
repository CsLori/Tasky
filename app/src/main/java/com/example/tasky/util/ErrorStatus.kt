package com.example.tasky.util

data class ErrorStatus(
    val hasError: Boolean,
    val errorMsg: UiText? = null,
)