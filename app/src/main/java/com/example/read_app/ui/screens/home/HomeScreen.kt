package com.example.read_app.ui.screens.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.read_app.ui.components.ArticleCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    application: Application,
    onOpenDetail: (String) -> Unit,
    onOpenSaved: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val viewModel = remember { HomeViewModel(application) }
    val state: HomeState by viewModel.state.collectAsState(initial = HomeState())
    val items = viewModel.pagingFlow.collectAsLazyPagingItems()
    
    LaunchedEffect(state.selectedCategory) {
        if (items.itemCount > 0) {

        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text("Haberler") },
            actions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Ara")
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Ayarlar")
                }
                TextButton(onClick = onOpenSaved) { Text("Kaydedilenler") }
            }
        )
        
        CategoryTabs(
            selectedCategory = state.selectedCategory,
            onCategorySelected = {
                viewModel.onCategoryChange(it)

            }
        )

        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
             Text(
                text = "Son senkron: ${state.lastSyncText}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (state.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        state.errorMessage?.let { msg ->
            ErrorBar(message = msg, onDismiss = viewModel::consumeError)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(count = items.itemCount) { index ->
                val article = items[index]
                if (article != null) {
                    ArticleCard(
                        article = article,
                        onClick = { onOpenDetail(article.id) },
                        onToggleBookmark = { viewModel.onToggleBookmark(article.id) }
                    )
                }
            }

            item {
                when (val refresh = items.loadState.refresh) {
                    is LoadState.Loading -> LoadingRow()
                    is LoadState.Error -> InlineError("Yüklenemedi: ${refresh.error.message}")
                    else -> Unit
                }
            }

            item {
                when (val append = items.loadState.append) {
                    is LoadState.Loading -> LoadingRow()
                    is LoadState.Error -> InlineError("Devamı yüklenemedi: ${append.error.message}")
                    else -> Unit
                }
            }

            item {
                val isEmpty = items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading
                if (isEmpty) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text("Sonuç yok veya yükleniyor...")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val categories = listOf(
        null to "Manşetler",
        "business" to "Ekonomi",
        "entertainment" to "Eğlence",
        "health" to "Sağlık",
        "science" to "Bilim",
        "sports" to "Spor",
        "technology" to "Teknoloji"
    )

    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.first == selectedCategory },
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEachIndexed { index, (key, title) ->
            Tab(
                selected = (key == selectedCategory),
                onClick = { onCategorySelected(key) },
                text = { Text(title) }
            )
        }
    }
}


@Composable
private fun LoadingRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun InlineError(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}

@Composable
private fun ErrorBar(message: String, onDismiss: () -> Unit) {
    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = message, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    }
}
