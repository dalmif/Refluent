package io.kayt.refluent.feature.home.adddeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddDeckUiState())
    val state = _state.asStateFlow()

    fun addNewDeck() {
        viewModelScope.launch {
            val name = state.value.name
            val color1 = state.value.color1
            val color2 = state.value.color2
            deckRepository.addNewDeck(name, Pair(color1, color2))
        }
    }

    fun onNameChanges(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onColor1Changes(color1: Int) {
        _state.value = _state.value.copy(color1 = color1)
    }

    fun onColor2Changes(color2: Int) {
        _state.value = _state.value.copy(color2 = color2)
    }
}

data class AddDeckUiState(
    val name: String = "",
    val color1: Int = 0,
    val color2: Int = 0
)
