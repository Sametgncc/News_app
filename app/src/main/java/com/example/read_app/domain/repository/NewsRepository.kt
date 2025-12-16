package com.example.read_app.domain.repository


import com.example.read_app.domain.model.Article
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData


interface NewsRepository {
    fun observeAll(): Flow<List<Article>>
    fun observeBookmarked(): Flow<List<Article>>

    suspend fun getById(id: String): Article?

    suspend fun refreshTopHeadlines(category: String? = null)
    suspend fun toggleBookmark(id: String)

    fun pagedAll(): Flow<PagingData<Article>>

    fun pagedBookmarked(): Flow<PagingData<Article>>

    suspend fun search(query: String, language: String = "tr")


}
