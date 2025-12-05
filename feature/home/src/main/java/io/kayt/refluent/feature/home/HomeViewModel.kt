package io.kayt.refluent.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.model.Deck
import io.kayt.core.model.LiveEditState
import io.kayt.core.model.SearchResultCard
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    deckRepository: DeckRepository,
    liveEditRepository: LiveEditRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val state: StateFlow<HomeUiState> =
        combine(
            deckRepository.getAllDeck(),
            liveEditRepository.getLiveEditState()
        ) { decks, liveEditState ->
            if (decks.isEmpty()) {
                HomeUiState.Empty(liveEditState)
            } else {
                HomeUiState.Success(decks, liveEditState)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading(LiveEditState.Disabled())
            )

    @OptIn(FlowPreview::class)
    val searchResult = searchQuery
        .debounce(200)
        .mapLatest {
            if (it.isBlank()) {
                SearchResult.NoSearch
            } else {
                SearchResult.SearchContent(deckRepository.searchCardGlobally(it))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SearchResult.NoSearch
        )

    fun onQueryChange(query: String) {
        searchQuery.value = query
    }
}

sealed interface SearchResult {
    data object NoSearch : SearchResult
    data class SearchContent(val cards: List<SearchResultCard>) : SearchResult
}

sealed class HomeUiState(open val liveEditState: LiveEditState) {
    data class Empty(override val liveEditState: LiveEditState) : HomeUiState(liveEditState)
    data class Loading(override val liveEditState: LiveEditState) : HomeUiState(liveEditState)
    data class Success(val decks: List<Deck>, override val liveEditState: LiveEditState) :
        HomeUiState(liveEditState)
}