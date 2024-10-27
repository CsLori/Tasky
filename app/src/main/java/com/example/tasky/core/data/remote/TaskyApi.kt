package com.example.tasky.core.data.remote

import com.example.tasky.agenda.agenda_data.remote.dto.AgendaResponse
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.onboarding.onboarding_data.remote.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.RegisterRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface TaskyApi {

    @POST("/register")
    suspend fun register(@Body registerBody: RegisterRequest)

    @POST("/login")
    suspend fun login(@Body loginBody: LoginRequest): LoginResponse

    @GET("/agenda")
    suspend fun getAgenda(@Query("time") time: Long): AgendaResponse

    @POST("/event")
    suspend fun addEvent()

    @POST("/task")
    suspend fun addTask(@Body taskBody: AgendaItem.Task)

    @PUT("/task")
    suspend fun updateTask(@Body taskBody: AgendaItem.Task)

    @DELETE("/task")
    suspend fun deleteTaskById(@Query("taskId") taskId: String)

    @GET("/logout")
    suspend fun logout()

    @GET("/authenticate")
    suspend fun authenticateUser()

}