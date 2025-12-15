package com.example.read_app.data.local.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.read_app.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {

    // Listeyi tarihe göre en yeni -> en eski
    @Query("SELECT * FROM articles ORDER BY publishedAtEpochMs DESC")
    fun observeAll(): Flow<List<ArticleEntity>>

    // Sadece kaydedilenleri getir
    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAtEpochMs DESC")
    fun observeBookmarked(): Flow<List<ArticleEntity>>

    // Detay ekranı için tek haber
    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ArticleEntity?

    // Online’dan gelen listeyi cache’lemek için toplu insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ArticleEntity>)

    // Tek kayıt güncelleme
    @Update
    suspend fun update(item: ArticleEntity)

    // Cache temizlemek istersen
    @Query("DELETE FROM articles WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarked()

    // Tam tablo temizleme
    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun countArticles(): Int

    @Query("SELECT id FROM articles WHERE isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<String>

    @Query("DELETE FROM articles WHERE id LIKE 'seed-%'")
    suspend fun deleteSeedArticles()

    @Query("SELECT * FROM articles ORDER BY publishedAtEpochMs DESC")
    fun pagingSourceAll(): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAtEpochMs DESC")
    fun pagingSourceBookmarked(): PagingSource<Int, ArticleEntity>

}
