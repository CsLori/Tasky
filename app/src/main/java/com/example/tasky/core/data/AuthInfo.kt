package com.example.tasky.core.data

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val userName: String,
    val email: String,
    val accessTokenExpirationTimestamp: Long
)