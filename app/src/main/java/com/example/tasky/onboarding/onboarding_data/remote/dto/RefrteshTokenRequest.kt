package com.example.tasky.onboarding.onboarding_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenBody(val refreshToken: String?, val userId: String?)