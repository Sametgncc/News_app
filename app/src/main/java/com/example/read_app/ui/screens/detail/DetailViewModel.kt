package com.example.read_app.ui.screens.detail


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.read_app.core.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    app: Application,
    private val articleId: String
) : AndroidViewModel(app) {

    private val repo = AppModule.provideNewsRepository(app.applicationContext)

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val article = repo.getById(articleId)
            _state.update {
                it.copy(isLoading = false, article = article, errorMessage = if (article == null) "Haber bulunamadı" else null)
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            repo.toggleBookmark(articleId)
            // toggle sonrası tekrar çekip state güncelle
            val updated = repo.getById(articleId)
            _state.update { it.copy(article = updated) }
        }
    }
}
