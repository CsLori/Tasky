package com.example.tasky.onboarding.onboarding_data.repository

import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.remote.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.RegisterRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginResponse
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import java.util.concurrent.CancellationException

class DefaultUserRepository(
    private val api: TaskyApi
) : UserRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit, TaskyError> {
        return try {
            api.register(
                RegisterRequest(
                    fullName = name,
                    email = email,
                    password = password,
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()

            val error = e.asResult(::mapToTaskyError).error

            Result.Error(error)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse, TaskyError> {
        return try {
            val result = api.login(LoginRequest(email, password))
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()

            val error= e.asResult(::mapToTaskyError).error

            Result.Error(error)
        }
    }
}