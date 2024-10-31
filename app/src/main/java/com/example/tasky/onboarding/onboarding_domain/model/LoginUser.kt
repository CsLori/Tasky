package com.example.tasky.onboarding.onboarding_domain.model

data class LoginUser(
    val accessToken: String,
    val refreshToken: String,
    val fullName: String,
    val userId: String,
    val accessTokenExpirationTimestamp: Long
)