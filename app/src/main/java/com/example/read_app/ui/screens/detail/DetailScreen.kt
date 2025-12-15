package com.example.read_app.ui.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext

import android.content.ActivityNotFoundException
import android.widget.Toast



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    application: Application,
    articleId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val vm = remember(articleId) { DetailViewModel(application, articleId) }
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detay") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Geri") }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.article == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(state.errorMessage ?: "Haber bulunamadı") }

            else -> {
                val a = state.article!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Görsel
                    if (!a.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = a.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    Text(a.title, style = MaterialTheme.typography.headlineSmall)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!a.sourceName.isNullOrBlank()) {
                            AssistChip(onClick = {}, label = { Text(a.sourceName!!) })
                        }
                        if (a.isBookmarked) {
                            AssistChip(onClick = {}, label = { Text("Kaydedildi") })
                        }
                    }

                    if (!a.description.isNullOrBlank()) {
                        Text(a.description!!, style = MaterialTheme.typography.bodyLarge)
                    }

                    if (!a.content.isNullOrBlank()) {
                        Text(a.content!!, style = MaterialTheme.typography.bodyMedium)
                    }

                    // Aksiyonlar
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = vm::toggleBookmark) {
                            Text(if (a.isBookmarked) "Kaydı Kaldır" else "Kaydet")
                        }

                        if (!a.url.isNullOrBlank()) {
                            OutlinedButton(onClick = {
                                val url = a.url?.trim().orEmpty()
                                if (url.isNotBlank()) {
                                    val fixedUrl =
                                        if (url.startsWith("http")) url else "https://$url"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedUrl))
                                    context.startActivity(intent)
                                }
                            }) {
                                Text("Tarayıcıda Aç")
                            }

                        }
                    }

                    if (!a.url.isNullOrBlank()) {
                        OutlinedButton(onClick = {
                            val url = a.url?.trim().orEmpty()
                            if (url.isBlank()) {
                                Toast.makeText(context, "Paylaşılacak link yok.", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, a.title)
                                putExtra(Intent.EXTRA_TEXT, url)
                            }

                            try {
                                context.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(context, "Paylaşım için uygun uygulama bulunamadı.", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Paylaşım açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("Paylaş")
                        }

                    }
                }
            }
        }
    }
}
