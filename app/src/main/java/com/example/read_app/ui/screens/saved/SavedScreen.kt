package com.example.read_app.ui.screens.saved

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
fun SavedScreen(
    application: Application,
    onOpenDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val vm = remember { SavedViewModel(application) }
    val items = vm.pagingFlow.collectAsLazyPagingItems()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Kaydedilenler") },
            navigationIcon = {
               IconButton(onClick = onBack) {Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Geri")}
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(count = items.itemCount) { index ->
                val article = items[index]
                if (article != null) {
                    ArticleCard(
                        article = article,
                        onClick = { onOpenDetail(article.id) },
                        onToggleBookmark = { vm.onToggleBookmark(article.id) } // kaydı kaldırmak için yazdım
                    )
                }
            }

            item {
                when (val refresh = items.loadState.refresh) {
                    is LoadState.Loading -> Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is LoadState.Error -> Text("Yüklenemedi: ${refresh.error.message}")
                    else -> Unit
                }
            }

            item {
                when (val append = items.loadState.append) {
                    is LoadState.Loading -> Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is LoadState.Error -> Text("Devamı yüklenemedi: ${append.error.message}")
                    else -> Unit
                }
            }
        }

        if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Kaydedilmiş haber yok.")
            }
        }
    }
}
