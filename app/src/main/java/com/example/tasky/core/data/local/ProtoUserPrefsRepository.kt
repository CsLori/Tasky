package com.example.tasky.core.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.tasky.UserPreferences
import com.example.tasky.core.domain.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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
}