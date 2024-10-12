package com.example.tasky.core.di

import android.content.Context
import android.util.Log
import com.example.tasky.BuildConfig.API_KEY
import com.example.tasky.agenda.agenda_data.remote.AgendaRepositoryImpl
import com.example.tasky.agenda.agenda_domain.AgendaRepository
import com.example.tasky.core.local.ProtoUserPrefsRepository
import com.example.tasky.core.remote.TaskyApi
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskyModule {

    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val request = original.newBuilder()
                    .header("x-api-key", API_KEY)
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request).also {
                    Log.d("API Request", request.toString())
                }

            }.build()
    }

    @Provides
    @Singleton
    fun provideTaskyApi(client: OkHttpClient): TaskyApi {
        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepo(api: TaskyApi): DefaultUserRepository {
        return DefaultUserRepository(api)
    }

    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): ProtoUserPrefsRepository {
        return ProtoUserPrefsRepository(context)
    }

    @Provides
    @Singleton
    fun provideAgendaRepository(api: TaskyApi): AgendaRepository {
        return AgendaRepositoryImpl(api)
    }
}