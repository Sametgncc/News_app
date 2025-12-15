package com.example.read_app.domain.usecase

import com.example.read_app.domain.repository.NewsRepository


/**
 * Haber kaydet / kaydı kaldır (bookmark toggle).
 */
class ToggleBookmarkUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(articleId: String) {
        repository.toggleBookmark(articleId)
    }
}
