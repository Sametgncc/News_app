package com.example.read_app.core.di


import android.content.Context
import androidx.room.Room
import com.example.read_app.core.util.Constants
import com.example.read_app.core.util.SyncPrefs
import com.example.read_app.data.local.dao.ArticleDao
import com.example.read_app.data.local.db.AppDatabase
import com.example.read_app.data.remote.api.NewsApi
import com.example.read_app.data.repository.NewsRepositoryImpl
import com.example.read_app.domain.repository.NewsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory



object AppModule {
    // tutarlılık sağlamak için
    @Volatile private var db: AppDatabase? = null
    @Volatile private var okHttp: OkHttpClient? = null
    @Volatile private var retrofit: Retrofit? = null
    @Volatile private var newsApi: NewsApi? = null
    @Volatile private var newsRepository: NewsRepository? = null

    fun provideDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "read_app.db"
            )
            .fallbackToDestructiveMigration()
            .build().also { db = it }
        }
    }

    fun provideArticleDao(context: Context): ArticleDao {
        return provideDatabase(context).articleDao()
    }

    // hata ayıklama için yazdım
    fun provideOkHttpClient(): OkHttpClient {
        return okHttp ?: synchronized(this) {
            okHttp ?: run {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }

                OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            }.also { okHttp = it }
        }
    }
    // json verilerilerini kodlin nesnesine dönüştür işlemini yapan kütüphane moshi
    fun provideRetrofit(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: run {
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(provideOkHttpClient())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
            }.also { retrofit = it }
        }
    }


    fun provideNewsApi(): NewsApi {
        return newsApi ?: synchronized(this) {
            newsApi ?: provideRetrofit()
                .create(NewsApi::class.java)
                .also { newsApi = it }
        }
    }

    fun provideNewsRepository(context: Context): NewsRepository {
        return newsRepository ?: synchronized(this) {
            newsRepository ?: NewsRepositoryImpl(
                dao = provideArticleDao(context),
                api = provideNewsApi()
            ).also { newsRepository = it }
        }
    }

    @Volatile private var syncPrefs: SyncPrefs? = null

    fun provideSyncPrefs(context: Context): SyncPrefs {
        return syncPrefs ?: synchronized(this) {
            syncPrefs ?: SyncPrefs(context.applicationContext).also { syncPrefs = it }
        }
    }



}
