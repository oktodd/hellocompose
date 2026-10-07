package com.example.ch07.util

fun parseTags(text: String): List<String> {
    if (text.isBlank()) return emptyList()
    return text.split(",")
        .map { tag ->
            tag.trim()
                .removePrefix("#")
                .lowercase()
        }
        .filter { it.isNotEmpty() }
        .distinct()
}

fun formatTags(tags: List<String>): String {
    return tags.joinToString(", ")
}