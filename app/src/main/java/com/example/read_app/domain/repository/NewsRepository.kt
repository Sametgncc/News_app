package com.example.read_app.domain.repository


import com.example.read_app.domain.model.Article
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData
import com.example.read_app.core.util.Constants


interface NewsRepository {
    fun observeAll(): Flow<List<Article>>
    fun observeBookmarked(): Flow<List<Article>>

    suspend fun getById(id: String): Article?

    suspend fun refreshTopHeadlines(category: String? = null, country: String = Constants.DEFAULT_COUNTRY)
    suspend fun toggleBookmark(id: String)
    
    // Yeni: Tek bir makaleyi güncellemek için
    suspend fun update(article: Article)

    fun pagedAll(): Flow<PagingData<Article>>

    fun pagedBookmarked(): Flow<PagingData<Article>>

    suspend fun search(query: String, language: String = "tr")

    suspend fun fetchFullContent(url: String): String?
}
