package com.example.tasky.onboarding.onboarding_data.remote

import java.io.Serializable

data class RegisterBody(
    val fullName: String,
    val email: String,
    val password: String
) : Serializable
