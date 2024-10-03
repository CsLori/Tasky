package com.example.tasky.util

data class ErrorStatus(
    val isError: Boolean,
    val errorMsg: UiText? = null,
)