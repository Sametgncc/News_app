package com.example.read_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,

    val title: String,
    val description: String?,
    val content: String?,
    val url: String?,
    val imageUrl: String?,
    val sourceName: String?,
    val publishedAtEpochMs: Long?,

    val isBookmarked: Boolean = false,

    val cachedAtEpochMs: Long = System.currentTimeMillis()
)
