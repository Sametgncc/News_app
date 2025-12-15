package com.example.read_app.ui.screens.home

import androidx.room.Query

data class HomeState(
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastSyncText: String = "—",
    val query: String = ""
)
