package com.example.tasky.onboarding.onboarding_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val fullName: String?,
    val userId: String?,
    val accessTokenExpirationTimestamp: Long?,
)
