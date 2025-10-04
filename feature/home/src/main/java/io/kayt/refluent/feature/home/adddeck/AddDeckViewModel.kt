package io.kayt.refluent.feature.home.adddeck

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val editingDeckId = savedStateHandle.toRoute<AddDeckRoute>().editingDeckId
    private val isEditMode: Boolean
        get() = editingDeckId != null

    private val _state = MutableStateFlow(AddDeckUiState())
    private val _event = Channel<AddDeckEvent>(64)
    val event: Flow<AddDeckEvent> = _event.receiveAsFlow()
    val state = _state.asStateFlow()

    fun addNewDeck() {
        viewModelScope.launch {
            val name = state.value.name
            val color = state.value.color
            if (!isEditMode) {
                deckRepository.addNewDeck(name, Pair(color, COLOR_FIXED))
            } else {
                deckRepository.updateDeck(editingDeckId!!, name, Pair(color, COLOR_FIXED))
            }
            _event.trySend(AddDeckEvent.DeckAddedSuccessfully)
        }
    }

    init {
        if (isEditMode) {
            viewModelScope.launch {
                val deck = deckRepository.getDeckById(editingDeckId!!).first()
                _state.value.color = deck.colors.first
                _state.value.name = deck.name
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            deckRepository.removeDeck(editingDeckId!!)
            _event.trySend(AddDeckEvent.DeckDeletedSuccessfully)
        }
    }

    fun onNameChanges(name: String) {
        _state.value.name =
            name.substring(0..(name.length - 1).coerceAtMost(30)).trimStart().replace("\n", "")
    }
}

@Stable
class AddDeckUiState {
    var color: Int by mutableStateOf(0)
    var name: String by mutableStateOf("")
}

sealed interface AddDeckEvent {
    data object DeckAddedSuccessfully : AddDeckEvent
    data object DeckDeletedSuccessfully : AddDeckEvent
}