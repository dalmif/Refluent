package io.kayt.refluent.feature.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    deckRepository: DeckRepository
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
                cards = cards
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = DeckUiState.Loading
        )
}

sealed interface DeckUiState {
    object Loading : DeckUiState
    data class Success(val deck: Deck, val cards: List<Card>) : DeckUiState
}