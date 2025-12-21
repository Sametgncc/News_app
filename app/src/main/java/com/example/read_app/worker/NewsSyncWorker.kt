package com.example.read_app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.read_app.core.di.AppModule
import kotlinx.coroutines.flow.firstOrNull

/**
 * NewsSyncWorker:
 * Arkaplanda (periyodik) çalışıp API'den haberleri çekerek Room cache'i günceller.
 * Önce başlıkları, sonra da her haberin tam içeriğini indirerek çevrimdışı okumayı sağlar.
 */
class NewsSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = AppModule.provideNewsRepository(applicationContext)

            repo.refreshTopHeadlines()

            val articlesToUpdate = repo.observeAll().firstOrNull()?.filter {
                it.content.isNullOrBlank() || it.content.length < 500
            }

            articlesToUpdate?.forEach { article ->
                if (!article.url.isNullOrBlank()) {
                    val fullContent = repo.fetchFullContent(article.url!!)
                    if (!fullContent.isNullOrBlank()) {
                        // Sadece daha uzun bir içerik geldiyse güncelle
                        if (fullContent.length > (article.content?.length ?: 0)) {
                            val updatedArticle = article.copy(content = fullContent)
                            repo.update(updatedArticle)
                        }
                    }
                }
            }

            AppModule.provideSyncPrefs(applicationContext).setLastSync()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
