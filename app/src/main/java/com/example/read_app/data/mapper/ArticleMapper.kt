package com.example.read_app.data.mapper


import com.example.read_app.core.util.IdUtil
import com.example.read_app.core.util.TimeParser
import com.example.read_app.data.local.entity.ArticleEntity
import com.example.read_app.data.remote.dto.ArticleDto
import com.example.read_app.domain.model.Article

fun ArticleDto.toEntity(
    bookmarkedIds: Set<String>
): ArticleEntity {

    val safeTitle = title?.trim().orEmpty()
    val safeUrl = url?.trim()

    val computedId = IdUtil.stableIdFrom(
        (safeUrl ?: safeTitle) + "|" + (publishedAt ?: "")
    )

    return ArticleEntity(
        id = computedId,
        title = if (safeTitle.isNotBlank()) safeTitle else "(Başlık yok)",
        description = description,
        content = content,
        url = safeUrl,
        imageUrl = urlToImage,
        sourceName = source?.name,
        publishedAtEpochMs = TimeParser.isoToEpochMs(publishedAt),
        isBookmarked = bookmarkedIds.contains(computedId),
        cachedAtEpochMs = System.currentTimeMillis()
    )
}


fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        sourceName = sourceName,
        publishedAtEpochMs = publishedAtEpochMs,
        isBookmarked = isBookmarked
    )
}

fun Article.toEntity(
    cachedAtEpochMs: Long = System.currentTimeMillis()
): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        sourceName = sourceName,
        publishedAtEpochMs = publishedAtEpochMs,
        isBookmarked = isBookmarked,
        cachedAtEpochMs = cachedAtEpochMs
    )
}
