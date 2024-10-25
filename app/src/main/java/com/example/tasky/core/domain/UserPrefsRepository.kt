package com.example.tasky.core.domain

import com.example.tasky.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    // Access token
    suspend fun updateAccessToken(accessToken: String)

    suspend fun getAccessToken(): String

    // Refresh token
    suspend fun updateRefreshToken(refreshToken: String)

    suspend fun getRefreshToken(): String

    // UserId
    suspend fun updateUserId(userId: String)

    suspend fun getUserId(): String

    // Access token expiration timestamp
    suspend fun updateAccessTokenExpirationTimestamp(timestamp: Long)

    suspend fun getAccessTokenExpirationTimestamp(): Long

}