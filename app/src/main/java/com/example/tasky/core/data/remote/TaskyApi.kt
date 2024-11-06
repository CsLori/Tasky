package com.example.tasky.core.data.remote

import com.example.tasky.agenda.agenda_data.remote.dto.AgendaResponse
import com.example.tasky.agenda.agenda_data.remote.dto.AttendeeExistDto
import com.example.tasky.agenda.agenda_data.remote.dto.EventResponse
import com.example.tasky.agenda.agenda_data.remote.dto.TaskSerialized
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.TaskyError
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginRequest
import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginUserResponse
import com.example.tasky.onboarding.onboarding_data.remote.dto.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TaskyApi {

    @POST("/register")
    suspend fun register(@Body registerBody: RegisterRequest)

    @POST("/login")
    suspend fun login(@Body loginBody: LoginRequest): LoginUserResponse

    @GET("/agenda")
    suspend fun getAgenda(@Query("time") time: Long): AgendaResponse

    @Multipart
    @POST("/event")
    suspend fun addEvent(
        @Part("create_event_request") createEventRequest: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): Result<EventResponse, TaskyError>

    @DELETE("/event")
    suspend fun deleteEvent(@Query("eventId") eventId: String)

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

    @GET("/attendee")
    suspend fun getAttendee(@Query("email") email: String): AttendeeExistDto

    @DELETE("/attendee")
    suspend fun deleteAttendee(@Query("eventId") eventId: String)
}