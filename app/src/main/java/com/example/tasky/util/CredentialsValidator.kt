package com.example.tasky.util

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.example.tasky.R

class CredentialsValidator {

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    fun validateName(name: String): ErrorStatus {
        return when {
            name.trim().isEmpty() -> {
                ErrorStatus(true, UiText.StringResource(R.string.This_field_is_required))
            }

            name.length in 4..50 -> {
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
            var hasDigit = false
            var hasLetter = false
            var hasLower = false
            var hasUpper = false

            for (char in password) {
                when {
                    char.isDigit() -> hasDigit = true
                    char.isLowerCase() -> hasLower = true
                    char.isUpperCase() -> hasUpper = true
                    char.isLetter() -> hasLetter = true
                }
                if (hasDigit && hasLetter && hasLower && hasUpper) {
                    break
                }
            }

            hasDigit && hasLower && hasUpper
        }

        return when {
            password.trim().isEmpty() -> {
                ErrorStatus(true, UiText.StringResource(R.string.This_field_is_required))
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