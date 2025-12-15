package com.example.read_app.domain.usecase


data class NewsUseCases(
    val getArticles: GetArticlesUseCase,
    val refreshTopHeadlines: RefreshTopHeadlinesUseCase,
    val toggleBookmark: ToggleBookmarkUseCase,
    val searchEverything: SearchEverythingUseCase

)
