package com.example.tasky.core.data.remote

import com.example.tasky.onboarding.onboarding_data.remote.LoginBody
import com.example.tasky.onboarding.onboarding_data.remote.LoginResponse
import com.example.tasky.onboarding.onboarding_data.remote.RefreshTokenBody
import com.example.tasky.onboarding.onboarding_data.remote.RegisterBody
import com.example.tasky.onboarding.onboarding_data.remote.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskyApi {

    @POST("/register")
    suspend fun register(@Body registerBody: RegisterBody)

    @POST("/login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse

    @POST("/accessToken")
    suspend fun refreshToken(@Body body: RefreshTokenBody): TokenResponse

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
    }
}