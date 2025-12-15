package com.example.read_app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.read_app.core.util.Constants
import com.example.read_app.data.local.dao.ArticleDao
import com.example.read_app.data.mapper.toDomain
import com.example.read_app.data.mapper.toEntity
import com.example.read_app.data.remote.api.NewsApi
import com.example.read_app.domain.model.Article
import com.example.read_app.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.paging.PagingData
import androidx.paging.map
import android.util.Log


import com.example.read_app.BuildConfig


class NewsRepositoryImpl(


    private val dao: ArticleDao,
    private val api: NewsApi
) : NewsRepository {


    override fun observeAll(): Flow<List<Article>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeBookmarked(): Flow<List<Article>> =
        dao.observeBookmarked().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Article? =
        dao.getById(id)?.toDomain()

    override suspend fun refreshTopHeadlines() {
        val apiKey = BuildConfig.NEWS_API_KEY

        val bookmarkedIds = dao.getBookmarkedIds().toSet()

        dao.deleteNonBookmarked()

        val response = api.searchEverything(
            q = Constants.DEFAULT_QUERY,
            pageSize = Constants.PAGE_SIZE,
            page = 1,
            apiKey = apiKey,
            language = Constants.DEFAULT_COUNTRY
        )


        Log.d("NEWS_REFRESH", "status=${response.status} total=${response.totalResults} articles=${response.articles?.size}")

        val entities = (response.articles ?: emptyList())
            .map { it.toEntity(bookmarkedIds) }

        Log.d("NEWS_REFRESH", "entities=${entities.size}")

        if (entities.isNotEmpty()) {
            dao.deleteSeedArticles()
        }
        dao.upsertAll(entities)


    }

    override suspend fun search(query: String, language: String) {
        val apiKey = BuildConfig.NEWS_API_KEY
        val bookmarkedIds = dao.getBookmarkedIds().toSet()

        dao.deleteNonBookmarked()

        val response = api.searchEverything(
            q = query,
            language = language,
            pageSize = Constants.PAGE_SIZE,
            page = 1,
            apiKey = apiKey
        )

        Log.d("NEWS_SEARCH", "q=$query status=${response.status} total=${response.totalResults} articles=${response.articles?.size}")

        val entities = (response.articles ?: emptyList())
            .map { it.toEntity(bookmarkedIds) }

        Log.d("NEWS_SEARCH", "entities=${entities.size}")

        if (entities.isNotEmpty()) {
            dao.deleteSeedArticles()
        }
        dao.upsertAll(entities)
    }


    override suspend fun toggleBookmark(id: String) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(isBookmarked = !current.isBookmarked))
    }

    override fun pagedAll(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { dao.pagingSourceAll() }   // ✅ ALL
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun pagedBookmarked(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.pagingSourceBookmarked() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }


}
