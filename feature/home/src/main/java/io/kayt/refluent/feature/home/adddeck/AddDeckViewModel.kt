package io.kayt.refluent.feature.home.adddeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddDeckUiState())
    private val _event = Channel<AddDeckEvent>(64)
    val event: Flow<AddDeckEvent> = _event.receiveAsFlow()
    val state = _state.asStateFlow()

    fun addNewDeck() {
        viewModelScope.launch {
            val name = state.value.name
            val color = state.value.color
            deckRepository.addNewDeck(name, Pair(color, COLOR_FIXED))
            _event.trySend(AddDeckEvent.DeckAddedSuccessfully)
        }
    }

    fun onNameChanges(name: String) {
        _state.value =
            _state.value.copy(name = name.substring(0..(name.length - 1).coerceAtMost(30)))
    }

    fun onColorChanges(color: Int) {
        println("mmd color came here : $color")
        _state.value = _state.value.copy(color = color)
    }
}

data class AddDeckUiState(
    val name: String = "",
    val color: Int = 0
)

sealed interface AddDeckEvent {
    data object DeckAddedSuccessfully : AddDeckEvent
}