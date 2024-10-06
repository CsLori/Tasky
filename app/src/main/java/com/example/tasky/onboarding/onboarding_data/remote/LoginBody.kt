package com.example.tasky.onboarding.onboarding_data.remote

import java.io.Serializable

data class LoginBody(
    val email: String,
    val password: String
) : Serializable
