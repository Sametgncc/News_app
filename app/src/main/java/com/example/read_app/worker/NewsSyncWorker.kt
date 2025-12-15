package com.example.read_app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.read_app.core.di.AppModule

/**
 * NewsSyncWorker:
 * Arkaplanda (periyodik) çalışıp API'den haberleri çekerek Room cache'i günceller.
 */
class NewsSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = AppModule.provideNewsRepository(applicationContext)
            repo.refreshTopHeadlines()

            AppModule.provideSyncPrefs(applicationContext).setLastSync()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

