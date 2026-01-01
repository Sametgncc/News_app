package com.example.read_app.ui.screens.home

import com.example.read_app.core.util.NewsType // NewsType'ı import et

data class HomeState(
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncText: String = "—",
    val query: String = "",
    val language: String = "tr",
    val selectedCategory: String? = null,
    val selectedNewsType: NewsType = NewsType.Local
)
