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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val generativeRepository: GenerativeRepository
) : ViewModel() {

    val route = savedStateHandle.toRoute<AddCardRoute>()
    val deckId = route.deckId
    val isEditingMode: Boolean
        get() = route.editingCardId != null

    val state = MutableStateFlow(AddCardUiState())
    private val _events = Channel<AddCardEvent>(64)
    val events = _events.receiveAsFlow()

    init {
        if (isEditingMode) {
            viewModelScope.launch {
                val card = deckRepository.getCardById(route.editingCardId!!)
                if (card != null) {
                    state.value = AddCardUiState(
                        frontSide = card.front,
                        backSide = card.back,
                        commentRichText = RichTextState().apply {
                            setHtml(card.comment)
                        }
                    )
                }
            }
        }
    }

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

    fun delete() {
        if (isEditingMode) {
            // This shouldn't be canceled even if the screen close
            GlobalScope.launch {
                deckRepository.deleteCard(route.editingCardId!!)
            }
        }
    }

    fun onAddCardButton() {
        val currentState = state.value
        if (currentState.frontSide.isBlank()){
            _events.trySend(AddCardEvent.CardNeedAtLeastFrontSide)
            return
        }
        viewModelScope.launch {
            if (!isEditingMode) {
                deckRepository.addNewCard(
                    deckId = deckId,
                    frontSide = currentState.frontSide,
                    backSide = currentState.backSide,
                    phonetic = phonetic.value ?: "",
                    comment = currentState.commentRichText.toHtml()
                )
            }
            else {
                deckRepository.updateCard(
                    cardId = route.editingCardId!!,
                    frontSide = currentState.frontSide,
                    backSide = currentState.backSide,
                    phonetic = phonetic.value ?: "",
                    comment = currentState.commentRichText.toHtml()
                )
            }
            _events.trySend(AddCardEvent.CardAddedSuccessfully)
        }
    }

    fun onAiGenerateClick(generate: AiGenerate) {
        val currentState = state.value
        val frontSide = currentState.frontSide
        if (frontSide.isBlank()) return
        viewModelScope.launch {
            val aiResult: Result<String> = when (generate) {
                is AiGenerate.MakeExampleSentences -> withLoading(0) {
                    generativeRepository.generateExampleSentences(frontSide)
                }


                is AiGenerate.MakeDefinition -> withLoading(1) {
                    generativeRepository.generateDefinition(frontSide)
                }


                is AiGenerate.Custom -> {
                    TODO("Not yet implemented")
                }
            }
            aiResult.onSuccess {
                val position = state.value.commentRichText.toText().length
                state.value.commentRichText.insertHtml(
                    (if (position == 0) "" else "<br>") + it,
                    state.value.commentRichText.toText().length
                )
            }.onFailure {
                _events.trySend(AddCardEvent.AiGeneratingFailed)
            }
        }
    }

    inline fun <T> withLoading(aiIndex: Int, block: () -> T): T {
        state.update {
            it.copy(aiButtonLoading = it.aiButtonLoading.withLoading(aiIndex))
        }
        val response = block()
        state.update {
            it.copy(aiButtonLoading = it.aiButtonLoading.withoutLoading(aiIndex))
        }
        return response
    }
}

data class AddCardUiState(
    val frontSide: String = "",
    val backSide: String = "",
    val aiButtonLoading: AiButtonLoading = AiButtonLoading.None,
    val commentRichText: RichTextState = RichTextState()
)

sealed interface AddCardEvent {
    data object AiGeneratingFailed : AddCardEvent
    data object CardNeedAtLeastFrontSide : AddCardEvent
    data object CardAddedSuccessfully : AddCardEvent
}

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