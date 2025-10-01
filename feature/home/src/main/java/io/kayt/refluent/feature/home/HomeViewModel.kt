package io.kayt.refluent.feature.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Deck
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    deckRepository: DeckRepository
) : ViewModel() {

    val state: StateFlow<HomeUiState> = deckRepository.getAllDeck()
        .map { decks ->
            if (decks.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Success(decks)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Empty
        )
}

sealed interface HomeUiState {
    data object Empty : HomeUiState
    data class Success(val decks: List<Deck>) : HomeUiState
}