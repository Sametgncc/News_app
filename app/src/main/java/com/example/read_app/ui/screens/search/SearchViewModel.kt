package com.example.read_app.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.read_app.core.di.AppModule
import com.example.read_app.core.util.Constants
import com.example.read_app.domain.model.Article
import com.example.read_app.domain.usecase.GetArticlesUseCase
import com.example.read_app.domain.usecase.NewsUseCases
import com.example.read_app.domain.usecase.RefreshTopHeadlinesUseCase
import com.example.read_app.domain.usecase.SearchEverythingUseCase
import com.example.read_app.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AppModule.provideNewsRepository(app.applicationContext)

    // Arama sonuçları da yine veritabanından gelecek
    // Ancak sadece arama yapıldığında tetiklenecek
    val pagingFlow: Flow<PagingData<Article>> = repository.pagedAll()

    private val useCases = NewsUseCases(
        getArticles = GetArticlesUseCase(repository),
        refreshTopHeadlines = RefreshTopHeadlinesUseCase(repository),
        toggleBookmark = ToggleBookmarkUseCase(repository),
        searchEverything = SearchEverythingUseCase(repository)
    )

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(text: String) {
        _state.update { it.copy(query = text) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(800L) // Biraz daha uzun debounce
            if (text.isNotBlank()) {
                performSearch(text)
            }
        }
    }

    private suspend fun performSearch(query: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            // Veritabanını silip yeni arama sonucunu kaydeder
            useCases.searchEverything(query, Constants.DEFAULT_COUNTRY)
            _state.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, errorMessage = e.message) }
        }
    }
    
    fun onToggleBookmark(id: String) {
        viewModelScope.launch {
            useCases.toggleBookmark(id)
        }
    }
}
