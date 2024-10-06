package com.example.tasky.core.util

import android.util.Patterns
import com.example.tasky.R

const val MIN_NAME_LENGTH = 4
const val MAX_NAME_LENGTH = 50
const val MIN_PASSWORD_LENGTH = 9

object CredentialsValidator {

    fun validateName(name: String): ErrorStatus {
        return when {
            name.trim().isEmpty() -> {
                ErrorStatus(true, UiText.StringResource(R.string.This_field_is_required))
            }

            name.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH -> {
                ErrorStatus(false)
            }

            else -> {
                ErrorStatus(true, UiText.StringResource(R.string.Name_length_error))
            }
        }
    }

    fun validateEmail(email: String): ErrorStatus {
        return when {
            email.trim().isEmpty() -> {
                ErrorStatus(true, UiText.StringResource(R.string.This_field_is_required))
            }

            (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Please_enter_a_valid_email_address_error)
                )
            }

            else -> {
                ErrorStatus(false)
            }
        }
    }

    fun validatePassword(password: String): ErrorStatus {
        val containsLettersDigitsLowerCaseAndUpperCase = run {
            val hasLower = password.any { it.isLowerCase() }
            val hasUpper = password.any { it.isUpperCase() }
            val hasNumber = password.any { it.isDigit() }

            hasLower && hasUpper && hasNumber
        }

//        val containsLettersDigitsLowerCaseAndUpperCase = run {
//            var hasDigit = false
//            var hasLetter = false
//            var hasLower = false
//            var hasUpper = false
//
//            for (char in password) {
//                when {
//                    char.isDigit() -> hasDigit = true
//                    char.isLowerCase() -> hasLower = true
//                    char.isUpperCase() -> hasUpper = true
//                    char.isLetter() -> hasLetter = true
//                }
//                if (hasDigit && hasLetter && hasLower && hasUpper) {
//                    break
//                }
//            }
//
//            hasDigit && hasLower && hasUpper
//        }

        return when {
            password.trim().isEmpty() -> {
                ErrorStatus(true, UiText.StringResource(R.string.This_field_is_required))
            }

            password.trim().length < MIN_PASSWORD_LENGTH -> {
                ErrorStatus(
                    true,
                    UiText.StringResource(
                        R.string.Enter_at_least_x_characters_long,
                        MIN_PASSWORD_LENGTH
                    )
                )
            }

            containsLettersDigitsLowerCaseAndUpperCase -> {
                ErrorStatus(false)
            }

            else -> {
                ErrorStatus(
                    true,
                    UiText.StringResource(R.string.Password_error)
                )
            }

        }
    }
}