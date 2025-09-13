package io.kayt.refluent.feature.deck.addcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mohamedrejeb.richeditor.model.RichTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.DeckRepository
import io.kayt.refluent.core.data.GenerativeRepository
import io.kayt.refluent.core.data.VocabularyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val generativeRepository: GenerativeRepository
) : ViewModel() {

    val deckId = savedStateHandle.toRoute<AddCardRoute>().deckId
    val state = MutableStateFlow(AddCardUiState())

    @OptIn(FlowPreview::class)
    val phonetic = state
        .map { it.frontSide }
        .distinctUntilChanged()
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

    fun onAddCardButton() {
        val currentState = state.value
        viewModelScope.launch {
            deckRepository.addNewCard(
                deckId = deckId,
                frontSide = currentState.frontSide,
                backSide = currentState.backSide,
                comment = currentState.commentRichText.toHtml()
            )
        }
    }

    fun onAiGenerateClick(generate: AiGenerate) {
        val currentState = state.value
        viewModelScope.launch {
            val aiResult = when (generate) {
                is AiGenerate.MakeExampleSentences -> {
                    state.value = state.value.copy(aiButtonLoading = state.value.aiButtonLoading.withLoading(0))
                    val response = generativeRepository.generateExampleSentences(currentState.frontSide)
                    state.value = state.value.copy(aiButtonLoading = state.value.aiButtonLoading.withoutLoading(0))
                    response
                }

                is AiGenerate.MakeDefinition -> {
                    state.value = state.value.copy(aiButtonLoading = state.value.aiButtonLoading.withLoading(1))
                    val response = generativeRepository.generateDefinition(currentState.frontSide)
                    state.value = state.value.copy(aiButtonLoading = state.value.aiButtonLoading.withoutLoading(1))
                    response
                }

                is AiGenerate.Custom -> {
                    TODO("Not yet implemented")
                }
            }
            val position = state.value.commentRichText.toText().length
            state.value.commentRichText.insertHtml(
                (if (position == 0) "" else "<br>") + aiResult,
                state.value.commentRichText.toText().length
            )
        }
    }
}

data class AddCardUiState(
    val frontSide: String = "",
    val backSide: String = "",
    val aiButtonLoading: AiButtonLoading = AiButtonLoading.None,
    val commentRichText: RichTextState = RichTextState()
)

sealed interface AiGenerate {
    object MakeExampleSentences : AiGenerate
    object MakeDefinition : AiGenerate
    class Custom(val prompt: String) : AiGenerate
}

@JvmInline
value class AiButtonLoading private constructor(private val bits: Int) {
    fun isLoading(button: Int): Boolean = (bits and (1 shl button)) != 0

    fun withLoading(button: Int): AiButtonLoading = AiButtonLoading(bits or (1 shl button))

    fun withoutLoading(button: Int): AiButtonLoading =
        AiButtonLoading(bits and (1 shl button).inv())

    companion object {
        val None = AiButtonLoading(0)
    }
}