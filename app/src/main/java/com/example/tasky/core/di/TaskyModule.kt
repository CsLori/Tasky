package com.example.tasky.core.di

import android.content.Context
import com.example.tasky.Constants.BASE_URL
import com.example.tasky.agenda.agenda_data.di.BasicOkHttpClient
import com.example.tasky.agenda.agenda_data.remote.AgendaRepositoryImpl
import com.example.tasky.agenda.agenda_data.remote.AuthTokenInterceptor
import com.example.tasky.agenda.agenda_domain.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskyModule {

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @BasicOkHttpClient basicOkHttpClient: OkHttpClient,
        authTokenInterceptor: AuthTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                authTokenInterceptor.intercept(it)
            }.build()
    }

    @Provides
    @Singleton
    fun provideTaskyApi(client: OkHttpClient): TaskyApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): ProtoUserPrefsRepository {
        return ProtoUserPrefsRepository(context)
    }

    @Provides
    @Singleton
    fun provideUserRepo(api: TaskyApi): DefaultUserRepository {
        return DefaultUserRepository(api)
    }


    @Provides
    @Singleton
    fun provideAgendaRepository(api: TaskyApi): AgendaRepository {
        return AgendaRepositoryImpl(api)
    }
}