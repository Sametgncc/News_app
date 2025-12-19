package com.example.read_app.ui.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.material.icons.filled.ArrowBackIosNew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    application: Application,
    articleId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isReaderMode by remember { mutableStateOf(false) }

    val vm = remember(articleId) { DetailViewModel(application, articleId) }
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            if (!isReaderMode) {
                TopAppBar(
                    title = { Text("Detay") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Geri")
                        }
                    },
                    actions = {
                        val article = state.article
                        if (article != null && (!article.content.isNullOrBlank() || !article.description.isNullOrBlank())) {
                            IconButton(onClick = { isReaderMode = true }) {
                                Icon(Icons.Default.MenuBook, contentDescription = "Okuma Modu")
                            }
                        }
                    }
                )
            }
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
                if (isReaderMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(a.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                            IconButton(onClick = { isReaderMode = false }) {
                                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Okuma Modundan Çık")
                            }
                        }
                        val textToShow = a.content ?: a.description
                        if (!textToShow.isNullOrBlank()) {
                            Text(textToShow, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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

                        if (!a.content.isNullOrBlank() && a.content != a.description) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(a.content!!, style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = vm::toggleSpeaking) {
                                Icon(
                                    imageVector = if (state.isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = if (state.isSpeaking) "Durdur" else "Dinle",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(onClick = vm::toggleBookmark) {
                                Text(if (a.isBookmarked) "Kaldır" else "Kaydet")
                            }

                            if (!a.url.isNullOrBlank()) {
                                OutlinedButton(onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, a.title)
                                        putExtra(Intent.EXTRA_TEXT, a.url)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
                                }) {
                                    Text("Paylaş")
                                }
                            }
                        }
                        
                        if (!a.url.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val url = a.url!!.trim()
                                    val builder = CustomTabsIntent.Builder()
                                    val customTabsIntent = builder.build()
                                    customTabsIntent.launchUrl(context, Uri.parse(url))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tarayıcıda Oku")
                            }
                        }
                    }
                }
            }
        }
    }
}
