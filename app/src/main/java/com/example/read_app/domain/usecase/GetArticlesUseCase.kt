package com.example.read_app.domain.usecase


import com.example.read_app.domain.model.Article
import com.example.read_app.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesUseCase(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.observeAll()
}
