package io.kayt.refluent.feature.deck.addcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository
) : ViewModel() {

    val deckId = savedStateHandle.toRoute<AddCardRoute>().deckId
    val state = MutableStateFlow(AddCardUiState())

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