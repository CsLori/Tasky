package com.example.tasky.core.data.remote

import com.example.tasky.agenda.agenda_data.remote.dto.AgendaResponse
import com.example.tasky.agenda.agenda_data.remote.dto.TaskSerialized
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.RegisterRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginUserResponse
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
    suspend fun login(@Body loginBody: LoginRequest): LoginUserResponse

    @GET("/agenda")
    suspend fun getAgenda(@Query("time") time: Long): AgendaResponse

    @POST("/event")
    suspend fun addEvent()

    @POST("/task")
    suspend fun addTask(@Body taskBody: TaskSerialized)

    @PUT("/task")
    suspend fun updateTask(@Body taskBody: TaskSerialized)

    @DELETE("/task")
    suspend fun deleteTaskById(@Query("taskId") taskId: String)

    @GET("/logout")
    suspend fun logout()

    @GET("/authenticate")
    suspend fun authenticateUser()

}