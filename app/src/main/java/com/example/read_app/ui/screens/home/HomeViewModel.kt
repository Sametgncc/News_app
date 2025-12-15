package com.example.read_app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.read_app.core.di.AppModule
import com.example.read_app.data.local.seed.SeedArticles
import com.example.read_app.domain.model.Article
import com.example.read_app.domain.usecase.GetArticlesUseCase
import com.example.read_app.domain.usecase.NewsUseCases
import com.example.read_app.domain.usecase.RefreshTopHeadlinesUseCase
import com.example.read_app.domain.usecase.SearchEverythingUseCase
import com.example.read_app.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AppModule.provideNewsRepository(app.applicationContext)

    val pagingFlow: Flow<PagingData<Article>> = repository.pagedAll()

    private val useCases = NewsUseCases(
        getArticles = GetArticlesUseCase(repository),
        refreshTopHeadlines = RefreshTopHeadlinesUseCase(repository),
        toggleBookmark = ToggleBookmarkUseCase(repository),
        searchEverything = SearchEverythingUseCase(repository)

    )

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val syncPrefs = AppModule.provideSyncPrefs(app.applicationContext)

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    init {
        seedIfEmpty()
        observeLastSync()
    }

    private fun seedIfEmpty() {
        viewModelScope.launch {
            val dao = AppModule.provideArticleDao(getApplication())
            if (dao.countArticles() == 0) {
                dao.upsertAll(SeedArticles.sample())
            }
        }
    }

    private fun observeLastSync() {
        viewModelScope.launch {
            syncPrefs.lastSyncEpochMs.collect { epoch ->
                val text = if (epoch == null) "—"
                else formatter.format(Instant.ofEpochMilli(epoch))

                _state.update { it.copy(lastSyncText = text) }
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }

            runCatching { useCases.refreshTopHeadlines() }
                .onSuccess {
                    syncPrefs.setLastSync()
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = e.message ?: "Yenileme başarısız"
                        )
                    }
                }
        }
    }

    fun onToggleBookmark(articleId: String) {
        viewModelScope.launch {
            runCatching { useCases.toggleBookmark(articleId) }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Kaydetme başarısız") }
                }
        }
    }

    fun consumeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onQueryChange(text: String) {
        _state.update { it.copy(query = text) }
    }

    fun onSearch() {
        val q = state.value.query.trim()
        if (q.isBlank()) return
        val lang = state.value.language

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }

            runCatching { useCases.searchEverything(q, lang) }
                .onSuccess {
                    syncPrefs.setLastSync()
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshing = false, errorMessage = e.message ?: "Arama başarısız") }
                }
        }
    }


    fun onClearSearch() {
        _state.update { it.copy(query = "") }
        onRefresh()
    }

    fun onLanguageChange(lang: String) {
        _state.update { it.copy(language = lang) }
    }


}
