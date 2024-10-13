package com.example.tasky.agenda.agenda_data.remote

import com.example.tasky.BuildConfig.API_KEY
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.onboarding.onboarding_data.remote.RefreshTokenBody
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException

class AuthTokenInterceptor(
    private val userPrefsRepository: ProtoUserPrefsRepository,
    private val tokenRefreshApi: TokenRefreshApi
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val originalRequest = chain.request()

            val requestWithToken = originalRequest.newBuilder()
                .header("x-api-key", API_KEY)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${userPrefsRepository.getAccessToken()}")
                .method(originalRequest.method, originalRequest.body)
                .build()

            var response = chain.proceed(requestWithToken)

            if (response.code == 401) {
                response.close()

                val refreshRequest = RefreshTokenBody(
                    refreshToken = userPrefsRepository.getRefreshToken(),
                    userId = userPrefsRepository.getUserId()
                )

                val accessTokenResponse = try {
                    tokenRefreshApi.refreshToken(refreshRequest)
                } catch (e: IOException) {
                    return@runBlocking response
                } catch (e: HttpException) {
                    return@runBlocking response
                }

                userPrefsRepository.updateAccessToken(accessTokenResponse.accessToken.toString())

                val newRequest = originalRequest.newBuilder()
                    .header("x-api-key", API_KEY)
                    .header("Authorization", "Bearer ${accessTokenResponse.accessToken}")
                    .build()

                response = chain.proceed(newRequest)
            }
            response
        }
    }
}
