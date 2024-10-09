package com.example.tasky.onboarding.onboarding_data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val email: String,
    val password: String
)
