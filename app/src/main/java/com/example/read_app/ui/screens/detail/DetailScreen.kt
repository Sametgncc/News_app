package com.example.read_app.ui.screens.detail

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Eşit dağılım
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
                                val url = a.url?.trim().orEmpty()
                                if (url.isBlank()) {
                                    Toast.makeText(context, "Paylaşılacak link yok.", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, a.title)
                                    putExtra(Intent.EXTRA_TEXT, "$url")
                                }
                                try {
                                    context.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("Paylaş")
                            }
                        }
                    }
                    
                    if (!a.url.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = {
                                val url = a.url?.trim().orEmpty()
                                if (url.isNotBlank()) {
                                    val fixedUrl =
                                        if (url.startsWith("http")) url else "https://$url"

                                    // Chrome Custom Tabs ile aç
                                    try {
                                        val builder = CustomTabsIntent.Builder()
                                        val customTabsIntent = builder.build()
                                        customTabsIntent.launchUrl(context, Uri.parse(fixedUrl))
                                    } catch (e: Exception) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedUrl))
                                            context.startActivity(intent)
                                        } catch (e: ActivityNotFoundException) {
                                            Toast.makeText(context, "Tarayıcı bulunamadı", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
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
