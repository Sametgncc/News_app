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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

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

    override suspend fun refreshTopHeadlines(category: String?) {
        val apiKey = BuildConfig.NEWS_API_KEY
        val bookmarkedIds = dao.getBookmarkedIds().toSet()

        var response = api.getTopHeadlines(
            category = category,
            country = Constants.DEFAULT_COUNTRY,
            pageSize = Constants.PAGE_SIZE,
            page = 1,
            apiKey = apiKey
        )

        if ((response.articles.isNullOrEmpty()) && category != null) {
            Log.d("NEWS_REFRESH", "TopHeadlines boş döndü, SearchEverything deneniyor: $category")
            response = api.searchEverything(
                q = category,
                language = Constants.DEFAULT_COUNTRY, // "tr"
                pageSize = Constants.PAGE_SIZE,
                page = 1,
                apiKey = apiKey
            )
        }

        Log.d("NEWS_REFRESH", "cat=$category status=${response.status} total=${response.totalResults} articles=${response.articles?.size}")

        val entities = (response.articles ?: emptyList())
            .map { it.toEntity(bookmarkedIds) }


        if (entities.isNotEmpty()) {
            dao.deleteNonBookmarked()
            dao.deleteSeedArticles()
            dao.upsertAll(entities)
        } else {
            Log.w("NEWS_REFRESH", "API'den hiç veri gelmedi, mevcut veriler korunuyor.")

        }
    }

    override suspend fun search(query: String, language: String) {
        val apiKey = BuildConfig.NEWS_API_KEY
        val bookmarkedIds = dao.getBookmarkedIds().toSet()

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

        if (entities.isNotEmpty()) {
            dao.deleteNonBookmarked()
            dao.deleteSeedArticles()
            dao.upsertAll(entities)
        }
    }

    override suspend fun toggleBookmark(id: String) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(isBookmarked = !current.isBookmarked))
    }
    
    override suspend fun update(article: Article) {
        val existing = dao.getById(article.id)
        if (existing != null) {
              val updatedEntity = existing.copy(
                 content = article.content, // Scraping ile gelen uzun metin

             )
             dao.update(updatedEntity)
             Log.d("REPO_UPDATE", "Haber güncellendi: ${article.title.take(20)}... Yeni içerik uzunluğu: ${article.content?.length}")
        } else {
            Log.e("REPO_UPDATE", "Güncellenecek haber bulunamadı ID: ${article.id}")
        }
    }

    override fun pagedAll(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { dao.pagingSourceAll() }
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
    
    override suspend fun fetchFullContent(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("JSOUP", "Bağlanılıyor: $url")
                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                var elements = doc.select("article p")
                
                if (elements.isEmpty()) {
                    elements = doc.select("p")
                }

                val sb = StringBuilder()
                for (el in elements) {
                    val text = el.text().trim()
                    if (text.length > 50 && !text.contains("copyright", ignoreCase = true)) {
                        sb.append(text).append("\n\n")
                    }
                }
                
                val result = sb.toString()
                Log.d("JSOUP", "İçerik çekildi. Uzunluk: ${result.length}")
                
                if (result.isEmpty()) null else result
                
            } catch (e: Exception) {
                Log.e("JSOUP_ERROR", "Error fetching content: ${e.message}")
                null
            }
        }
    }
}
