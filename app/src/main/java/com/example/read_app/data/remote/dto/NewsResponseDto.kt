package com.example.read_app.data.remote.dto

import com.squareup.moshi.Json

data class NewsResponseDto(
    @Json(name = "status") val status: String?,
    @Json(name = "totalResults") val totalResults: Int?,
    @Json(name = "articles") val articles: List<ArticleDto>?
)
