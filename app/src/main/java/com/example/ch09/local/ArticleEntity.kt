package com.example.ch09.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val category: String,
    val title: String,
    val author: String?,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val position: Int,
    val cachedAt: Long
)