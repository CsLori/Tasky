package com.example.tasky.core.data.remote

import com.example.tasky.agenda.agenda_data.AgendaResponse
import com.example.tasky.agenda.agenda_data.TaskBody
import com.example.tasky.onboarding.onboarding_data.remote.LoginBody
import com.example.tasky.onboarding.onboarding_data.remote.LoginResponse
import com.example.tasky.onboarding.onboarding_data.remote.RegisterBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TaskyApi {

    @POST("/register")
    suspend fun register(@Body registerBody: RegisterBody)

    @POST("/login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse

//    @POST("/accessToken")
//    suspend fun refreshToken(@Body accessTokenBody: RefreshTokenBody): TokenResponse

    @GET("/agenda")
    suspend fun getAgenda(@Query("time") time: Long): AgendaResponse

    @POST("/event")
    suspend fun addEvent()

    @POST("/task")
    suspend fun addTask(@Body taskBody: TaskBody)
}