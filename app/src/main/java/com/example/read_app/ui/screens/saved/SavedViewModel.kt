package com.example.read_app.ui.screens.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.read_app.core.di.AppModule
import com.example.read_app.domain.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SavedViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppModule.provideNewsRepository(app.applicationContext)

    val pagingFlow: Flow<PagingData<Article>> = repo.pagedBookmarked()

    fun onToggleBookmark(articleId: String) {
        viewModelScope.launch {
            repo.toggleBookmark(articleId)
        }
    }
}
