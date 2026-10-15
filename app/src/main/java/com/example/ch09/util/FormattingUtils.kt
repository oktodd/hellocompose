package com.example.ch09.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

// Overload 1: Menerima Long (timestamp / milidetik), cocok untuk offline.cachedAt
fun formatRelativeTime(timeMillis: Long): String {
    return try {
        val now = System.currentTimeMillis()
        val diffMillis = now - timeMillis

        val minutes = diffMillis / (1000 * 60)
        val hours = diffMillis / (1000 * 60 * 60)
        val days = diffMillis / (1000 * 60 * 60 * 24)

        when {
            minutes < 1 -> "Baru saja"
            minutes < 60 -> "$minutes menit lalu"
            hours < 24 -> "$hours jam lalu"
            days < 7 -> "$days hari lalu"
            else -> {
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                formatter.format(Date(timeMillis))
            }
        }
    } catch (e: Exception) {
        ""
    }
}

// Overload 2: Menerima String (format ISO 8601), cocok untuk article.publishedAt
fun formatRelativeTime(isoTime: String?): String {
    if (isoTime.isNullOrBlank()) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val pastDate = parser.parse(isoTime) ?: return isoTime
        formatRelativeTime(pastDate.time) // Memanggil fungsi overload Long di atas
    } catch (e: Exception) {
        isoTime
    }
}