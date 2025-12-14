package io.kayt.refluent.feature.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.core.model.ReviewMode
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    val deckId = savedStateHandle.toRoute<DeckRoute>().deckId
    val state = deckRepository
        .getCardsForDeck(deckId)
        .combine(deckRepository.getDeckById(deckId)) { cards, deck ->
            cards to deck
        }
        .map { (cards, deck) ->
            DeckUiState.Success(
                deck = deck,
                cards = cards,
                settings = DeckSetting(
                    reviewMode = deck.reviewMode
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = DeckUiState.Loading
        )

    fun updateReviewMode(reviewMode: ReviewMode) {
        viewModelScope.launch {
            val localState = state.value
            if (localState is DeckUiState.Success) {
                deckRepository.updateDeck(
                    deckId,
                    localState.deck.name,
                    Pair(localState.deck.colors.first, localState.deck.colors.second),
                    reviewMode
                )
            }
        }
    }
}

sealed interface DeckUiState {
    object Loading : DeckUiState
    data class Success(val deck: Deck, val cards: List<Card>, val settings: DeckSetting) :
        DeckUiState
}

data class DeckSetting(
    val reviewMode: ReviewMode
)