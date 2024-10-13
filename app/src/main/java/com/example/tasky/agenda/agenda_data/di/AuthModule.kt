package com.example.tasky.agenda.agenda_data.di

import com.example.tasky.Constants.BASE_URL
import com.example.tasky.agenda.agenda_data.remote.AuthTokenInterceptor
import com.example.tasky.agenda.agenda_data.remote.TokenRefreshApi
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    @BasicOkHttpClient
    fun provideBasicOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()
    }

    @Provides
    @Singleton
    fun provideRefreshTokenApi(@BasicOkHttpClient basicClient: OkHttpClient): TokenRefreshApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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