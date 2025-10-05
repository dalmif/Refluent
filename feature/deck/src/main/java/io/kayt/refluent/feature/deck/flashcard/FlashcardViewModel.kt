package io.kayt.refluent.feature.deck.flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val deckId = savedStateHandle.toRoute<FlashcardRoute>().deckId
    val state: StateFlow<FlashcardUiState> = deckRepository.getDueCardsForDeck(deckId).take(1)
        .combine(deckRepository.getDeckById(deckId).take(1)) { cards, deck ->
            FlashcardUiState.Success(cards, deck)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FlashcardUiState.Loading
        )

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
