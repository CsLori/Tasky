package com.example.tasky.core.util

data class ErrorStatus(
    val isError: Boolean,
    val errorMsg: UiText? = null,
)