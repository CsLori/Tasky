package com.example.tasky.onboarding.onboarding_domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.onboarding.onboarding_domain.model.LoginUser

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit, TaskyError>

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginUser, TaskyError>

    suspend fun logout(): Result<Unit, TaskyError>

    suspend fun authenticate(): Result<Unit, TaskyError>
}