package io.kayt.refluent.feature.deck.addcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import io.kayt.refluent.core.data.VocabularyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository,
    private val vocabularyRepository: VocabularyRepository,
) : ViewModel() {

    val deckId = savedStateHandle.toRoute<AddCardRoute>().deckId
    val state = MutableStateFlow(AddCardUiState())

    @OptIn(FlowPreview::class)
    val phonetic = state
        .map { it.frontSide }
        .debounce(300)
        .mapLatest {
            vocabularyRepository.getPhoneticForWord(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = null
        )

    fun onFrontSideChange(newValue: String) {
        state.value = state.value.copy(frontSide = newValue)
    }

    fun onBackSideChange(newValue: String) {
        state.value = state.value.copy(backSide = newValue)
    }

    fun onCommentChange(newValue: String) {
        state.value = state.value.copy(comment = newValue)
    }

    fun onAddCardButton() {
        val currentState = state.value
        viewModelScope.launch {
            deckRepository.addNewCard(
                deckId = deckId,
                frontSide = currentState.frontSide,
                backSide = currentState.backSide,
                comment = currentState.comment
            )
        }
    }
}

data class AddCardUiState(
    val frontSide: String = "",
    val backSide: String = "",
    val comment: String = ""
)