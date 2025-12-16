package com.example.read_app.ui.screens.detail

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.read_app.core.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class DetailViewModel(
    app: Application,
    private val articleId: String
) : AndroidViewModel(app), TextToSpeech.OnInitListener {

    private val repo = AppModule.provideNewsRepository(app.applicationContext)
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        load()
        tts = TextToSpeech(app.applicationContext, this)
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val article = repo.getById(articleId)
            _state.update {
                it.copy(isLoading = false, article = article, errorMessage = if (article == null) "Haber bulunamadı" else null)
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            repo.toggleBookmark(articleId)
            val updated = repo.getById(articleId)
            _state.update { it.copy(article = updated) }
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("tr", "TR"))
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                 tts?.setLanguage(Locale.US)
            }
            
            isTtsInitialized = true
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = true) }
                }

                override fun onDone(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }

                override fun onError(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }
            })
        }
    }

    fun toggleSpeaking() {
        if (!isTtsInitialized || tts == null) return
        val article = state.value.article ?: return

        if (state.value.isSpeaking) {
            // Durdur
            tts?.stop()
            _state.update { it.copy(isSpeaking = false) }
        } else {
            // Oku
            val textToRead = buildString {
                append(article.title)
                append(". ")
                article.description?.let { append(it).append(". ") }
                article.content?.let { append(it) }
            }
            
            tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "article_read_id")
            _state.update { it.copy(isSpeaking = true) }
        }
    }

    override fun onCleared() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onCleared()
    }
}
