package com.example.tasky.onboarding.onboarding_domain

import com.example.tasky.onboarding.onboarding_data.dto.LoginResponse

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    )

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse>
}