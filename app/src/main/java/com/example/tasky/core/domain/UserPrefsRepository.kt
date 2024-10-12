package com.example.tasky.core.domain

import com.example.tasky.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    // Access token
    suspend fun updateAccessToken(accessToken: String)

    suspend fun getAccessToken(): UserPreferences

    // Refresh token
    suspend fun updateRefreshToken(refreshToken: String)

    suspend fun getRefreshToken(): UserPreferences

    // UserId
    suspend fun updateUserId(userId: String)

    suspend fun getUserId(): UserPreferences

}