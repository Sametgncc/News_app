package com.example.read_app.data.local.seed


import com.example.read_app.data.local.entity.ArticleEntity

object SeedArticles {
    fun sample(): List<ArticleEntity> = listOf(
        ArticleEntity(
            id = "seed-1",
            title = "Offline/Online Haber Uygulaması - Seed 1",
            description = "Bu seed veri: UI + Room + Flow akışını test etmek için eklendi.",
            content = "Detay içerik (seed).",
            url = null,
            imageUrl = null,
            sourceName = "SeedSource",
            publishedAtEpochMs = System.currentTimeMillis() - 60_000,
            isBookmarked = false
        ),
        ArticleEntity(
            id = "seed-2",
            title = "Bookmark test: Kaydet/Kaldır çalışmalı",
            description = "Karttaki 'Kaydet' butonuna basınca isBookmarked değişir.",
            content = "Detay içerik (seed).",
            url = null,
            imageUrl = null,
            sourceName = "SeedSource",
            publishedAtEpochMs = System.currentTimeMillis() - 120_000,
            isBookmarked = true
        ),
        ArticleEntity(
            id = "seed-3",
            title = "Sonraki adım: Retrofit ile gerçek haber çekme",
            description = "Seed veri tamam, bir sonraki adımda API bağlayacağız.",
            content = "Detay içerik (seed).",
            url = null,
            imageUrl = null,
            sourceName = "SeedSource",
            publishedAtEpochMs = System.currentTimeMillis() - 180_000,
            isBookmarked = false
        )
    )
}
