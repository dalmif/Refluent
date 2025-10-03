package io.kayt.refluent.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.model.Deck
import io.kayt.refluent.core.data.DeckRepository
import io.kayt.refluent.core.data.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    deckRepository: DeckRepository,
    userRepository: UserRepository,
) : ViewModel() {

    val state: StateFlow<HomeUiState> =
        combine(
            flowOf(userRepository.getUsername()),
            deckRepository.getAllDeck()
        )
        { user, decks ->
            if (decks.isEmpty()) {
                HomeUiState.Empty(name = user ?: "")
            } else {
                HomeUiState.Success(decks, name = user ?: "")
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )
}

sealed interface HomeUiState {

    data class Empty(val name: String) : HomeUiState
    data object Loading : HomeUiState
    data class Success(val decks: List<Deck>, val name: String) : HomeUiState
}