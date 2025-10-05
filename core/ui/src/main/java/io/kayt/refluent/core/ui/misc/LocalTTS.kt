package io.kayt.refluent.core.ui.misc

import android.annotation.SuppressLint
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

interface ITTSManager {
    val isAvailable: Boolean
    fun speak(text: String)
}

class TTSManager : ITTSManager {
    override var isAvailable: Boolean by mutableStateOf(false)
        internal set

    private var textToSpeech: TextToSpeech? = null
    internal fun initialize(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.ENGLISH)
                isAvailable = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            } else {
                isAvailable = false
            }
        }
    }

    override fun speak(text: String) {
        if (isAvailable && textToSpeech != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    internal fun destroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

@Composable
fun provideTTSManager(): ProvidedValue<ITTSManager> {
    val context = LocalContext.current
    val manager = remember { TTSManager().apply { initialize(context) } }
    DisposableEffect(Unit) {
        onDispose {
            manager.destroy()
        }
    }
    return LocalTtsManager provides manager
}

private class NoOpTTSManager(override val isAvailable: Boolean) : ITTSManager {
    @Suppress("EmptyFunctionBlock")
    override fun speak(text: String) {}
}

// For using in preview
@Composable
fun NoOpTTSManagerScope(isAvailable: Boolean = false, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTtsManager provides remember(isAvailable) {
            NoOpTTSManager(isAvailable)
        }
    ) {
        content()
    }
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalTtsManager =
    staticCompositionLocalOf<ITTSManager> { error("LocalTtsManager is not provided in the hierarchy") }
