package com.example.tasky.onboarding.onboarding_data.repository

import android.util.Log
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.util.Result
import com.example.tasky.onboarding.onboarding_data.remote.LoginBody
import com.example.tasky.onboarding.onboarding_data.remote.LoginResponse
import com.example.tasky.onboarding.onboarding_data.remote.RegisterBody
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.onboarding.util.AuthError
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.CancellationException

class DefaultUserRepository(
    private val api: TaskyApi
) : UserRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit, AuthError> {
        return try {
            api.register(
                RegisterBody(
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

            val error = when (e) {
                is HttpException -> when (e.code()) {
                    409 -> AuthError.Register.EMAIL_ALREADY_EXISTS
                    else -> AuthError.General.SERVER_ERROR
                }

                is IOException -> AuthError.General.NO_INTERNET
                else -> AuthError.General.SERVER_ERROR
            }
            Result.Error(error)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse, AuthError> {
        return try {
            val result = api.login(LoginBody(email, password))
            Log.d("DDD", result.userId.toString())
            Log.d("DDD", result.fullName.toString())
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()

            val error = when (e) {
                is HttpException -> when (e.code()) {
                    401 -> AuthError.Login.INVALID_CREDENTIALS
                    403 -> AuthError.General.UNAUTHORIZED
                    404 -> AuthError.Login.ACCOUNT_LOCKED
                    else -> AuthError.General.SERVER_ERROR
                }

                is IOException -> AuthError.General.NO_INTERNET
                else -> AuthError.General.SERVER_ERROR
            }
            Result.Error(error)
        }
    }
}