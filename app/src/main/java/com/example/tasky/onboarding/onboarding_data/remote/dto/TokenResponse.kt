package com.example.tasky.onboarding.onboarding_data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(val accessToken: String?, val expirationTimestamp: Long?)
