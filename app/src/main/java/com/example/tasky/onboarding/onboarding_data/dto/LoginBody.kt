package com.example.tasky.onboarding.onboarding_data.dto

import java.io.Serializable

data class LoginBody(
    val email: String,
    val password: String
) : Serializable
