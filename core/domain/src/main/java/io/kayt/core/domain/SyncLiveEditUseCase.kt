package io.kayt.core.domain

import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.domain.repository.VocabularyRepository
import io.kayt.core.model.CardOperation
import io.kayt.core.model.DeckOperation
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
    private val decksAddedFromRemote: MutableMap<String, Long> = mutableMapOf()

    suspend fun sync() {
        coroutineScope {
            launch {
                liveEditRepository.observeCardOperation().collect { operation ->
                    when (operation) {
                        is CardOperation.Create -> deckRepository.addNewCard(
                            deckId = when (val id = operation.deckId) {
                                is DeckOperation.DeckId.LocalId -> id.id
                                is DeckOperation.DeckId.RemoteId -> decksAddedFromRemote[id.id]!!
                            },
                            frontSide = operation.front,
                            backSide = operation.back,
                            comment = operation.comment,
                            phonetic = vocabularyRepository.getPhoneticForWord(operation.front)
                                ?: ""
                        ).also { cardAddedFromRemote[operation.remoteId] = it }

                        is CardOperation.Delete ->
                            when (val id = operation.cardId) {
                                is CardOperation.CardId.LocalId -> id.id
                                is CardOperation.CardId.RemoteId -> cardAddedFromRemote[id.id]
                            }?.let {
                                deckRepository.deleteCard(it)
                            }?.also {
                                val cardID = operation.cardId
                                if (cardID is CardOperation.CardId.RemoteId)
                                    cardAddedFromRemote.remove(cardID.id)
                            }

                        is CardOperation.Update ->
                            when (val id = operation.cardId) {
                                is CardOperation.CardId.LocalId -> id.id
                                is CardOperation.CardId.RemoteId -> cardAddedFromRemote[id.id]
                            }?.let {
                                deckRepository.updateCard(
                                    it,
                                    frontSide = operation.front,
                                    backSide = operation.back,
                                    comment = operation.comment,
                                    phonetic = vocabularyRepository.getPhoneticForWord(operation.front)
                                        ?: ""
                                )
                            }

                    }
                }
            }

            launch {
                liveEditRepository.observeDeckOperation().collect { operation ->
                    when (operation) {
                        is DeckOperation.Create -> deckRepository.addNewDeck(
                            operation.name,
                            0xFFEFE3B1.toInt() to 0xFFEFE3B1.toInt()
                        ).also { decksAddedFromRemote[operation.remoteId] = it }

                        is DeckOperation.Delete -> TODO()
                        is DeckOperation.Update -> TODO()
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
                                cards = deckRepository.getAllCards().first(),
                                deckRemoteIds = decksAddedFromRemote.toList(),
                                cardRemoteIds = cardAddedFromRemote.toList()
                            )
                        }
                    }
                }
            }
        }
    }
}