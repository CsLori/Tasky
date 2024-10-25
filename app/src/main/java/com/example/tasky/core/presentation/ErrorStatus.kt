package com.example.tasky.core.presentation

data class ErrorStatus(
    val hasError: Boolean,
    val errorMsg: UiText? = null,
)