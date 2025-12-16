package com.example.read_app.ui.screens.search

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
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
fun SearchScreen(
    application: Application,
    onOpenDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { SearchViewModel(application) }
    val state by viewModel.state.collectAsState()
    val items = viewModel.pagingFlow.collectAsLazyPagingItems()
    

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text("Haber ara...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Clear, "Temizle")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        
        Column(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.errorMessage?.let { msg ->
               Text(
                   text = msg,
                   color = MaterialTheme.colorScheme.error,
                   modifier = Modifier.padding(16.dp)
               )
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
                    if (items.loadState.refresh is LoadState.Loading && !state.isLoading) {
                         Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                             CircularProgressIndicator()
                         }
                    }
                }
                
                item {
                    if (items.itemCount == 0 && !state.isLoading && state.query.isNotBlank()) {
                         Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Sonuç bulunamadı.")
                        }
                    } else if (state.query.isBlank() && items.itemCount == 0) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Arama yapmak için bir kelime girin.")
                        }
                    }
                }
            }
        }
    }
}
