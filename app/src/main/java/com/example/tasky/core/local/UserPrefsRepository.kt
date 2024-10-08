package com.example.tasky.core.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.tasky.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class UserPrefsRepository(context: Context) {
    private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
        fileName = "user_prefs.pb",
        serializer = UserSerializer
    )

    private val userPrefsStore: DataStore<UserPreferences> = context.userPreferencesStore

    val userPreferencesFlow: Flow<UserPreferences> = userPrefsStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("Error", exception.message.toString())
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateAccessToken(accessToken: String) {
        userPrefsStore.updateData { preference ->
            preference.toBuilder().setAccessToken(accessToken).build()
        }
    }

    suspend fun getAccessToken() = userPrefsStore.data.first()

}