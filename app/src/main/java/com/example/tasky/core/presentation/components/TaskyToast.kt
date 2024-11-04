package com.example.tasky.core.presentation.components

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, message: Int) {
    Toast.makeText(
        context,
        context.getString(message),
        Toast.LENGTH_LONG
    ).show()
}