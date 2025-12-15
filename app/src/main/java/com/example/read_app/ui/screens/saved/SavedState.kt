package com.example.read_app.ui.screens.saved


import com.example.read_app.domain.model.Article

data class SavedState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null
)
