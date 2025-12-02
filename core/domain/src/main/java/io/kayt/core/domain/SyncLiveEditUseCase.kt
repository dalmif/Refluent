package io.kayt.core.domain

import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.domain.repository.VocabularyRepository
import io.kayt.core.model.CardOperation
import io.kayt.core.model.SyncOperation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncLiveEditUseCase @Inject constructor(
    private val liveEditRepository: LiveEditRepository,
    private val deckRepository: DeckRepository,
    private val vocabularyRepository: VocabularyRepository
) {
    private val cardAddedFromRemote: MutableMap<String, Long> = mutableMapOf()

    suspend fun sync() {
        coroutineScope {
            launch {
                liveEditRepository.observeCardOperation().collect { operation ->
                    when (operation) {
                        is CardOperation.Create -> deckRepository.addNewCard(
                            deckId = operation.deckId,
                            frontSide = operation.front,
                            backSide = operation.back,
                            comment = operation.comment,
                            phonetic = vocabularyRepository.getPhoneticForWord(operation.front)
                                ?: ""
                        ).also { cardAddedFromRemote[operation.remoteId] = it }

                        is CardOperation.Delete -> deckRepository.deleteCard(
                            when (val id = operation.cardId) {
                                is CardOperation.CardId.LocalId -> id.id
                                is CardOperation.CardId.RemoteId -> cardAddedFromRemote[id.id]!!
                            }
                        ).also {
                            if (operation.cardId is CardOperation.CardId.RemoteId)
                                cardAddedFromRemote.remove((operation.cardId as CardOperation.CardId.RemoteId).id)
                        }

                        is CardOperation.Update -> deckRepository.updateCard(
                            when (val id = operation.cardId) {
                                is CardOperation.CardId.LocalId -> id.id
                                is CardOperation.CardId.RemoteId -> cardAddedFromRemote[id.id]!!
                            },
                            frontSide = operation.front,
                            backSide = operation.back,
                            comment = operation.comment,
                            phonetic = vocabularyRepository.getPhoneticForWord(operation.front)
                                ?: ""
                        )

                    }
                }
            }

            launch {
                liveEditRepository.observeSyncOperation().collect {
                    when (it) {
                        is SyncOperation.PeerConnected,
                        SyncOperation.SyncRequested -> {
                            liveEditRepository.sendDecksAndCards(
                                decks = deckRepository.getAllDeck().first(),
                                cards = deckRepository.getAllCards().first()
                            )
                        }
                    }
                }
            }
        }
    }
}