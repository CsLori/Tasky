package com.example.tasky.onboarding.onboarding_data.repository

import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.core.domain.asResult
import com.example.tasky.core.domain.mapToTaskyError
import com.example.tasky.onboarding.onboarding_data.dto_mappers.toLoginUser
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.RegisterRequest
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.onboarding.onboarding_domain.model.LoginUser
import com.example.tasky.util.Logger
import java.util.concurrent.CancellationException

class DefaultUserRepository(
    private val api: TaskyApi,
    private val userPrefsRepository: ProtoUserPrefsRepository
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
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to register a user: %s", e.message)
            val error = e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginUser, TaskyError> {
        return try {
            val result = api.login(LoginRequest(email, password))
            Result.Success(result.toLoginUser().copy(email = email))
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to login a user: %s", e.message)
            val error= e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun logout(): Result<Unit, TaskyError> {
        return try {
            userPrefsRepository.updateAccessToken("")
            api.logout()
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to logout a user: %s", e.message)
            val error= e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }

    override suspend fun authenticate(): Result<Unit, TaskyError> {
        return try {
            val result = api.authenticateUser()
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Logger.e(e, "An error occurred while trying to authenticate a user: %s", e.message)
            val error= e.asResult(::mapToTaskyError).error
            Result.Error(error)
        }
    }
}