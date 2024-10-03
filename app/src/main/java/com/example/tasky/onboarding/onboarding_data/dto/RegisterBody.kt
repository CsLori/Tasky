package com.example.tasky.onboarding.onboarding_data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterBody(
    @SerializedName("full_name")
    val fullName: String,
    val email: String,
    val password: String
) : Serializable
