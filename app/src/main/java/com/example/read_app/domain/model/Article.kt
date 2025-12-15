package com.example.read_app.domain.model

data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String?,
    val imageUrl: String?,
    val sourceName: String?,
    val publishedAtEpochMs: Long?,
    val isBookmarked: Boolean
)