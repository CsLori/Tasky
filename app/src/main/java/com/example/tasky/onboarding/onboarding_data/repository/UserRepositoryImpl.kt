package com.example.tasky.onboarding.onboarding_data.repository

import com.example.tasky.core.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.dto.LoginBody
import com.example.tasky.onboarding.onboarding_data.dto.LoginResponse
import com.example.tasky.onboarding.onboarding_data.dto.RegisterBody
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import java.util.concurrent.CancellationException

class UserRepositoryImpl(
    private val api: TaskyApi
) : UserRepository {
    override suspend fun register(name: String, email: String, password: String) {
        try {
            api.register(
                RegisterBody(
                    fullName = name,
                    email = email,
                    password = password,
                )
            )
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val result = api.login(LoginBody(email, password))
//            result.refreshToken
            Result.success(result)
            TODO()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()
            Result.failure(e)
        }
    }
}