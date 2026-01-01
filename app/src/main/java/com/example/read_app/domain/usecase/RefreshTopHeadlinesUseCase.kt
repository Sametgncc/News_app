package com.example.read_app.domain.usecase


import com.example.read_app.domain.repository.NewsRepository
import com.example.read_app.core.util.Constants

class RefreshTopHeadlinesUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(category: String? = null, country: String = Constants.DEFAULT_COUNTRY) {
        repository.refreshTopHeadlines(category, country)
    }
}
