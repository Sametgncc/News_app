package com.example.read_app.domain.usecase


import com.example.read_app.domain.repository.NewsRepository

class RefreshTopHeadlinesUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke() {
        repository.refreshTopHeadlines()
    }
}
