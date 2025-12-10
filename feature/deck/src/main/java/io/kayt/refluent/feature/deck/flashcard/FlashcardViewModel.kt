package io.kayt.refluent.feature.deck.flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.core.model.ReviewMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val deckId = savedStateHandle.toRoute<FlashcardRoute>().deckId
    val state: StateFlow<FlashcardUiState> = deckRepository.getDueCardsForDeck(deckId).take(1)
        .combine(deckRepository.getDeckById(deckId).take(1)) { cards, deck ->
            FlashcardUiState.Success(
                cards = makeCardsBasedOnReviewMode(cards, deck.reviewMode),
                deck = deck
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FlashcardUiState.Loading
        )

    fun markCardAsGood(card: Card) {
        viewModelScope.launch {
            if (!isDeckDualSidedReview) {
                deckRepository.saveReviewResult(card, remembered = true)
            }
        }
    }

    fun markCardAsBad(card: Card) {
        viewModelScope.launch {
            deckRepository.saveReviewResult(card, remembered = false)
        }
    }

    private fun makeCardsBasedOnReviewMode(cards: List<Card>, reviewMode: ReviewMode): List<Card> {
        val cards = cards.shuffled()
        val sortedCard = when (reviewMode) {
            ReviewMode.FrontFirst -> cards
            ReviewMode.BackFirst -> cards.map { it.copy(front = it.back, back = it.front) }
            ReviewMode.ShuffleSides -> {
                val halfCardCount = cards.size / 2
                val shouldReverseFirstHalf = Random.nextInt(0..1) == 0
                val firstHalf = cards.subList(0, halfCardCount)
                val secondHalf = cards.subList(halfCardCount, cards.size)
                if (shouldReverseFirstHalf)
                    firstHalf.map { it.copy(front = it.back, back = it.front) } + secondHalf
                else
                    firstHalf + secondHalf.map { it.copy(front = it.back, back = it.front) }
            }

            ReviewMode.DualSided -> cards.map { it.copy(front = it.back, back = it.front) } + cards
        }
        return sortedCard.shuffled()
    }

    private val isDeckDualSidedReview: Boolean
        get() {
            val currentState = state.value
            return if (currentState is FlashcardUiState.Success) {
                currentState.deck.reviewMode == ReviewMode.DualSided
            } else false
        }
}

sealed interface FlashcardUiState {
    object Loading : FlashcardUiState
    data class Success(val cards: List<Card>, val deck: Deck) : FlashcardUiState
}
