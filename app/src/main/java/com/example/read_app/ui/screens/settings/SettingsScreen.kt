package com.example.read_app.ui.screens.settings

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.read_app.core.di.AppModule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    application: Application,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val syncPrefs = remember { AppModule.provideSyncPrefs(context) }
    val isDarkTheme by syncPrefs.isDarkTheme.collectAsState(initial = false)
    
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Önbelleği Temizle") },
            text = { Text("Kaydedilen haberler dışındaki tüm veriler silinecek. Devam edilsin mi?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val dao = AppModule.provideArticleDao(context)
                            dao.deleteNonBookmarked()
                            Toast.makeText(context, "Önbellek temizlendi", Toast.LENGTH_SHORT).show()
                        }
                        showDialog = false
                    }
                ) { Text("Temizle") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsHeader("Görünüm")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Karanlık Mod",
                    subtitle = "Uygulama temasını değiştir",
                    trailing = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { isChecked -> 
                                scope.launch {
                                    syncPrefs.setDarkTheme(isChecked)
                                }
                            }
                        )
                    },
                    onClick = { 
                        scope.launch {
                            syncPrefs.setDarkTheme(!isDarkTheme)
                        }
                    }
                )
            }

            item { Divider() }

            item {
                SettingsHeader("Veri ve Depolama")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Önbelleği Temizle",
                    subtitle = "Yer kaplayan eski haberleri siler",
                    onClick = { showDialog = true }
                )
            }

            item { Divider() }

            item {
                SettingsHeader("Hakkında")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Uygulama Sürümü",
                    subtitle = "v1.0.0",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = trailing,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun Divider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
}
