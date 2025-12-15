package com.example.read_app.ui.screens.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction

import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.read_app.ui.components.ArticleCard




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    application: Application,
    onOpenDetail: (String) -> Unit,
    onOpenSaved: () -> Unit
) {
    val viewModel = remember { HomeViewModel(application) }
    val state: HomeState by viewModel.state.collectAsState(initial = HomeState())
    val items = viewModel.pagingFlow.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text("Haberler") },
            actions = {
                TextButton(onClick = onOpenSaved) { Text("Kaydedilenler") }
                TextButton(onClick = {
                    viewModel.onRefresh()
                    items.refresh()
                }) { Text("Yenile") }
            }
        )

        SearchBarCard(
            query = state.query,
            lastSyncText = state.lastSyncText,
            language = state.language,
            onLanguageChange = viewModel::onLanguageChange,
            onQueryChange = viewModel::onQueryChange,
            onSearch = {
                viewModel.onSearch()
                items.refresh()
            },
            onClear = {
                viewModel.onClearSearch()
                items.refresh()
            }
        )


        // Üstte ince progress
        if (state.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Hata mesajı
        state.errorMessage?.let { msg ->
            ErrorBar(message = msg, onDismiss = viewModel::consumeError)
        }

        // Liste
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

            // İlk yükleme (refresh)
            item {
                when (val refresh = items.loadState.refresh) {
                    is LoadState.Loading -> LoadingRow()
                    is LoadState.Error -> InlineError("Yüklenemedi: ${refresh.error.message}")
                    else -> Unit
                }
            }

            // Devamı (append)
            item {
                when (val append = items.loadState.append) {
                    is LoadState.Loading -> LoadingRow()
                    is LoadState.Error -> InlineError("Devamı yüklenemedi: ${append.error.message}")
                    else -> Unit
                }
            }

            // Boş ekran
            item {
                val isEmpty = items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading
                if (isEmpty) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sonuç yok. Farklı bir arama deneyebilirsin.")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBarCard(
    query: String,
    lastSyncText: String,
    language: String,
    onLanguageChange: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ✅ Dil seçimi (TR / EN)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = language == "tr",
                    onClick = { onLanguageChange("tr") },
                    label = { Text("TR") }
                )
                FilterChip(
                    selected = language == "en",
                    onClick = { onLanguageChange("en") },
                    label = { Text("EN") }
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ara: yapay zeka, ekonomi, apple…") },



                trailingIcon = {
                    Row {
                        if (query.isNotBlank()) {
                            TextButton(onClick = onClear) { Text("Temizle") }
                        }
                        TextButton(onClick = onSearch) { Text("Ara") }
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Son senkron: $lastSyncText",
                style = MaterialTheme.typography.labelSmall
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
