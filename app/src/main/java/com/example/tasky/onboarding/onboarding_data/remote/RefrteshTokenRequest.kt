package com.example.tasky.onboarding.onboarding_data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenBody(val refreshToken: String?, val userId: String?)