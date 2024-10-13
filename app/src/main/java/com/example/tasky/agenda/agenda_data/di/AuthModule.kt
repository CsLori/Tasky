package com.example.tasky.agenda.agenda_data.di

import com.example.tasky.agenda.agenda_data.remote.AuthTokenInterceptor
import com.example.tasky.agenda.agenda_data.remote.TokenRefreshApi
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideRefreshTokenApi(basicClient: OkHttpClient): TokenRefreshApi {
        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(basicClient)
            .build()
            .create(TokenRefreshApi::class.java)
    }
    @Provides
    @Singleton
    fun provideAuthTokenInterceptor(
        userPrefsRepository: ProtoUserPrefsRepository,
        refreshTokenApi: TokenRefreshApi
    ): AuthTokenInterceptor {
        return AuthTokenInterceptor(userPrefsRepository, refreshTokenApi)
    }


}