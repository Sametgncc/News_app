package com.example.read_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.work.PeriodicWorkRequestBuilder
import com.example.read_app.ui.navigation.AppNavGraph
import com.example.read_app.ui.theme.Read_AppTheme
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.read_app.worker.NewsSyncWorker


class MainActivity : ComponentActivity() {
    private fun scheduleNewsSync() {
        // Sadece internet bağlantısı olduğunda çalışsın
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 6 saatte bir tekrar etsin
        val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Görevi sıraya koy (Eğer zaten varsa güncelleme - KEEP)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "news_sync",
            ExistingPeriodicWorkPolicy.KEEP, 
            request
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Arka plan senkronizasyonunu başlat
        scheduleNewsSync()
        
        setContent {
            Read_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)
                    )
                    AppNavGraph(
                        application = application,
                        modifier = Modifier.fillMaxSize()

                    )
                }
            }
        }
    }
}
