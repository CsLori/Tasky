package com.example.tasky.onboarding.onboarding_domain

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    )
}