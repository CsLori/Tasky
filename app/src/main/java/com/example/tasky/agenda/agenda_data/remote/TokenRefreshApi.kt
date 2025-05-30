package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.onboarding.onboarding_data.remote.dto.RefreshTokenBody
import com.example.tasky.onboarding.onboarding_data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshApi {
    @POST("/accessToken")
    suspend fun refreshToken(@Body body: RefreshTokenBody): TokenResponse
}