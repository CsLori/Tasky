package com.example.tasky.core.domain

import com.example.tasky.UserPreferences
import com.example.tasky.core.data.AuthInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

    // UserName
    suspend fun updateUserName(userName: String)

    suspend fun getUserName(): String

    // UserId
    suspend fun updateUserEmail(email: String)

    suspend fun getUserEmail(): String

    // Access token expiration timestamp
    suspend fun updateAccessTokenExpirationTimestamp(timestamp: Long)

    suspend fun getAccessTokenExpirationTimestamp(): Long

    // Has seen notification prompt
    suspend fun updateHasSeenNotificationPrompt(hasSeenNotificationPrompt: Boolean)

    suspend fun getHasSeenNotificationPrompt(): Boolean

    // Session count
    suspend fun updateSessionCount(sessionCount: Int)

    suspend fun getSessionCount(): Int

    val authInfo: StateFlow<AuthInfo?>
}