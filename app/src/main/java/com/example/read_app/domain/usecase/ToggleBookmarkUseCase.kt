package com.example.read_app.domain.usecase

import com.example.read_app.domain.repository.NewsRepository


// haber kaydı ve silme için
class ToggleBookmarkUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(articleId: String) {
        repository.toggleBookmark(articleId)
    }
}
