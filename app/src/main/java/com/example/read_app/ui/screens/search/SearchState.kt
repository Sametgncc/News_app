package com.example.read_app.ui.screens.search

import com.example.read_app.domain.model.Article

data class SearchState(
    val query: String = "",
    val results: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
