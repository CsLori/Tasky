package com.example.tasky.core.domain

import com.example.tasky.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun updateAccessToken(accessToken: String)

    suspend fun getAccessToken(): UserPreferences

}