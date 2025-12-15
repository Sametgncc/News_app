package com.example.read_app.ui.screens.detail


import com.example.read_app.domain.model.Article

data class DetailState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)
