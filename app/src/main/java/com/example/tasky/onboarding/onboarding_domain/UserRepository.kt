package com.example.tasky.onboarding.onboarding_domain

import com.example.tasky.util.Result
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginResponse
import com.example.tasky.util.AuthError

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit, AuthError>

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse, AuthError>
}