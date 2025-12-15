package com.example.read_app.ui.screens.home

import androidx.room.Query
import org.intellij.lang.annotations.Language

data class HomeState(
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncText: String = "—",
    val query: String = "",
    val language: String = "tr"
)
