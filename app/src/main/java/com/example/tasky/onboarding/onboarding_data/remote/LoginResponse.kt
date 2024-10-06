package com.example.tasky.onboarding.onboarding_data.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("full_name")
    val fullName: String?,
    val userId: String?,
    @SerializedName("timestamp")
    val accessTokenExpirationTimestamp: Long?,
) : Serializable
