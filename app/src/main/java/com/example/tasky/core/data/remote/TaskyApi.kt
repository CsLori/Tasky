package com.example.tasky.core.data.remote

import com.example.tasky.agenda.agenda_data.remote.dto.AgendaResponse
import com.example.tasky.agenda.agenda_domain.model.Task
import com.example.tasky.onboarding.onboarding_data.remote.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginResponse
import com.example.tasky.onboarding.onboarding_data.remote.RefreshTokenBody
import com.example.tasky.onboarding.onboarding_data.remote.RegisterRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TaskyApi {

    @POST("/register")
    suspend fun register(@Body registerBody: RegisterRequest)

    @POST("/login")
    suspend fun login(@Body loginBody: LoginRequest): LoginResponse

    @POST("/accessToken")
    suspend fun refreshToken(@Body accessTokenBody: RefreshTokenBody): TokenResponse

    @GET("/agenda")
    suspend fun getAgenda(@Query("time") time: Long): AgendaResponse

    @POST("/event")
    suspend fun addEvent()

    @POST("/task")
    suspend fun addTask(@Body taskBody: Task)

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
    }
}