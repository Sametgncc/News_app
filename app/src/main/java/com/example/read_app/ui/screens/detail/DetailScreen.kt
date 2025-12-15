package com.example.read_app.ui.screens.detail


import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    application: Application,
    articleId: String,
    onBack: () -> Unit
) {
    val vm = remember(articleId) { DetailViewModel(application, articleId) }
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Detay") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Geri") } }
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.article == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.errorMessage ?: "Haber bulunamadı")
            }

            else -> {
                val a = state.article!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(a.title, style = MaterialTheme.typography.headlineSmall)
                    if (!a.sourceName.isNullOrBlank()) Text("Kaynak: ${a.sourceName}", style = MaterialTheme.typography.labelMedium)
                    if (!a.description.isNullOrBlank()) Text(a.description!!, style = MaterialTheme.typography.bodyLarge)
                    if (!a.content.isNullOrBlank()) Text(a.content!!, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(8.dp))

                    Button(onClick = vm::toggleBookmark) {
                        Text(if (a.isBookmarked) "Kaydı Kaldır" else "Kaydet")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = vm::toggleBookmark) {
                        Text(if (a.isBookmarked) "Kaydı Kaldır" else "Kaydet")
                    }

                    if (!a.url.isNullOrBlank()) {
                        OutlinedButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, a.url.toUri())
                            application.startActivity(intent)
                        }) {
                            Text("Tarayıcıda Aç")
                        }
                    }
                }

                if (!a.url.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, a.title)
                            putExtra(Intent.EXTRA_TEXT, a.url)
                        }
                        application.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
                    }) {
                        Text("Paylaş")
                    }
                }

            }
        }
    }
}
