package com.example.read_app.domain.usecase


import com.example.read_app.domain.repository.NewsRepository

class SearchEverythingUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(query: String, language: String = "tr") {
        repository.search(query, language)
    }
}
