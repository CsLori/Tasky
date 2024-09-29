package com.example.tasky.core.remote

import com.example.tasky.onboarding.onboarding_data.dto.RegisterBody
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskyApi {

    @POST("/register'")
    suspend fun register(@Body registerBody: RegisterBody)

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
    }
}