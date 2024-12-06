package com.example.tasky.core.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.tasky.UserPreferences
import com.example.tasky.core.data.AuthInfo
import com.example.tasky.core.domain.UserPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException

class ProtoUserPrefsRepository(context: Context) : UserPrefsRepository {

    private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
        fileName = "user_prefs.pb",
        serializer = UserSerializer
    )

    private val userPrefsStore: DataStore<UserPreferences> = context.userPreferencesStore

    override val userPreferencesFlow: Flow<UserPreferences> = userPrefsStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("Error", exception.message.toString())
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override val authInfo: StateFlow<AuthInfo?> = userPreferencesFlow.map { preferences ->
        if (preferences.accessToken.isNotEmpty()) {
            AuthInfo(
                accessToken = preferences.accessToken,
                refreshToken = preferences.refreshToken,
                userId = preferences.userId,
                userName = preferences.userName,
                email = preferences.email,
                accessTokenExpirationTimestamp = preferences.accessTokenExpirationTimestamp.toLongOrNull() ?: 0L
            )
        } else null
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    // Access token
    override suspend fun updateAccessToken(accessToken: String) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setAccessToken(accessToken).build()
        }
    }

    override suspend fun getAccessToken(): String = userPrefsStore.data.first().accessToken


    // Refresh token
    override suspend fun updateRefreshToken(refreshToken: String) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setRefreshToken(refreshToken).build()
        }
    }

    override suspend fun getRefreshToken(): String = userPrefsStore.data.first().refreshToken


    // UserId
    override suspend fun updateUserId(userId: String) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setUserId(userId).build()
        }
    }

    override suspend fun getUserId(): String = userPrefsStore.data.first().userId

    // UserName
    override suspend fun updateUserName(userName: String) {
        userPrefsStore.updateData { preference ->
           preference.toBuilder().setUserName(userName).build()
        }
    }

    override suspend fun getUserName(): String = userPrefsStore.data.first().userName

    // Email
    override suspend fun updateUserEmail(email: String) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setEmail(email).build()
        }
    }

    override suspend fun getUserEmail(): String = userPrefsStore.data.first().email

    // AccessTokenExpiryDate
    override suspend fun updateAccessTokenExpirationTimestamp(timestamp: Long) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setAccessTokenExpirationTimestamp(timestamp.toString()).build()
        }
    }

    override suspend fun getAccessTokenExpirationTimestamp(): Long {
        val timestampString = userPrefsStore.data.first().accessTokenExpirationTimestamp
        return timestampString.toLong()
    }

    // Has seen notification prompt
    override suspend fun updateHasSeenNotificationPrompt(hasSeenNotificationPrompt: Boolean) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setHasSeenNotificationPrompt(hasSeenNotificationPrompt).build()
        }
    }

    override suspend fun getHasSeenNotificationPrompt(): Boolean {
        return userPrefsStore.data.first().hasSeenNotificationPrompt
    }

    // Session count
    override suspend fun updateSessionCount(sessionCount: Int) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setSessionCount(sessionCount).build()
        }
    }

    override suspend fun getSessionCount(): Int {
        return userPrefsStore.data.first().sessionCount
    }
}