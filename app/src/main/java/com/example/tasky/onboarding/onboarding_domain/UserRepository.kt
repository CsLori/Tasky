package com.example.tasky.onboarding.onboarding_domain

import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginResponse
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit, TaskyError>

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse, TaskyError>
}