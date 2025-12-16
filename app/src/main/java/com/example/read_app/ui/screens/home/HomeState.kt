package com.example.read_app.ui.screens.home

data class HomeState(
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncText: String = "—",
    val query: String = "",
    val language: String = "tr",
    val selectedCategory: String? = null
)
