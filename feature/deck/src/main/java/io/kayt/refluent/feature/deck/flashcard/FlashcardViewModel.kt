package io.kayt.refluent.feature.deck.flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Card
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    val state: StateFlow<FlashcardUiState> =
        flowOf(savedStateHandle.toRoute<FlashcardRoute>().deckId)
            .flatMapLatest { deckRepository.getCardsForDeck(it) }
            .map { FlashcardUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FlashcardUiState.Loading
            )
}

sealed interface FlashcardUiState {
    object Loading : FlashcardUiState
    data class Success(val cards: List<Card>) : FlashcardUiState
}
