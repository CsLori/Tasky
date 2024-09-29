package com.example.tasky.onboarding.onboarding_data.repository

import com.example.tasky.core.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.dto.RegisterBody
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import java.util.concurrent.CancellationException

class UserRepositoryImpl(
    private val api: TaskyApi
) : UserRepository {
    override suspend fun register(name: String, email: String, password: String) {
        try {
            val result = api.register(
                RegisterBody(
                    fullName = name,
                    email = email,
                    password = password,
                )
            )
            Result.success(result)

        } catch (e: Exception) {
            if (e is CancellationException) {
                e.printStackTrace()
                throw e
            }
            Result.failure(e)
        }
    }
}