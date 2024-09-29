package com.example.tasky.core.di

import com.example.tasky.BuildConfig.API_KEY
import com.example.tasky.core.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.dto.RegisterBody
import com.example.tasky.onboarding.onboarding_data.repository.UserRepositoryImpl
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
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
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
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
    fun provideUserRepo(api: TaskyApi): UserRepositoryImpl {
        return UserRepositoryImpl(api)
    }
}