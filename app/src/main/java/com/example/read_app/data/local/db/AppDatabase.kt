package com.example.read_app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.read_app.data.local.dao.ArticleDao
import com.example.read_app.data.local.entity.ArticleEntity


@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
