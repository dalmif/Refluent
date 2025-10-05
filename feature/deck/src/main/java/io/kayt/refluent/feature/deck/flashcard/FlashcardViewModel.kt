package io.kayt.refluent.feature.deck.flashcard

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val deckId = savedStateHandle.toRoute<FlashcardRoute>().deckId
    val state: StateFlow<FlashcardUiState> = deckRepository.getDueCardsForDeck(deckId).take(1)
        .combine(deckRepository.getDeckById(deckId).take(1))
        { cards, deck ->
            FlashcardUiState.Success(cards, deck)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FlashcardUiState.Loading
        )

    private var textToSpeech: TextToSpeech? = null
    private val _isTtsInitialized = MutableStateFlow(false)
    val isTtsInitialized: StateFlow<Boolean> = _isTtsInitialized.asStateFlow()

    fun initializeTts(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.ENGLISH)
                _isTtsInitialized.value = result != TextToSpeech.LANG_MISSING_DATA && 
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            } else {
                _isTtsInitialized.value = false
            }
        }
    }

    fun speakText(text: String) {
        if (_isTtsInitialized.value && textToSpeech != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    fun markCardAsGood(card: Card) {
        viewModelScope.launch {
            deckRepository.saveReviewResult(card, 4)
        }
    }

    fun markCardAsBad(card: Card) {
        viewModelScope.launch {
            deckRepository.saveReviewResult(card, 0)
        }
    }
}

sealed interface FlashcardUiState {
    object Loading : FlashcardUiState
    data class Success(val cards: List<Card>, val deck: Deck) : FlashcardUiState
}
