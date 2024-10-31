package com.example.tasky.onboarding.onboarding_data.dto_mappers

import com.example.tasky.onboarding.onboarding_data.remote.dto.LoginUserResponse
import com.example.tasky.onboarding.onboarding_domain.model.LoginUser

fun LoginUserResponse.toLoginUser(): LoginUser {
    return LoginUser(
        accessToken = accessToken,
        refreshToken = refreshToken,
        fullName = fullName,
        userId = userId,
        accessTokenExpirationTimestamp = accessTokenExpirationTimestamp
    )
}

fun LoginUser.toLoginUserResponse(): LoginUserResponse {
    return LoginUserResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        fullName = fullName,
        userId = userId,
        accessTokenExpirationTimestamp = accessTokenExpirationTimestamp
    )
}