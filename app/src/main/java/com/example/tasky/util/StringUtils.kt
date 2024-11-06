package com.example.tasky.util


// Lorant Tamas Csuhai -> LC
// Lorant -> LO
fun getInitials(fullName: String): String {
    val trimmedName = fullName.trim()
    return when {
        trimmedName.isEmpty() -> ""
        trimmedName.contains(" ") -> {
            trimmedName.split(" ").let { nameParts ->
                "${nameParts.first().firstOrNull()?.uppercase() ?: ""}${nameParts.last().firstOrNull()?.uppercase() ?: ""}"
            }
        }
        trimmedName.length >= 2 -> trimmedName.substring(0, 2).uppercase()
        else -> trimmedName.uppercase()
    }
}