package com.example.ch07.util

import java.util.concurrent.TimeUnit

// Implementasi fungsi murni untuk menghitung selisih waktu relatif
fun formatRelativeTime(now: Long, then: Long): String {
    val diff = (now - then).coerceAtLeast(0)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        seconds < 60 -> "baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 30 -> "$days hari lalu"
        days < 365 -> "${days / 30} bulan lalu"
        else -> "${days / 365} tahun lalu"
    }
}