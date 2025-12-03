package io.kayt.refluent.core.data

import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.model.Card
import io.kayt.core.model.CardOperation
import io.kayt.core.model.Deck
import io.kayt.core.model.DeckOperation
import io.kayt.core.model.LiveEditState
import io.kayt.core.model.SyncOperation
import io.kayt.refluent.core.data.network.LiveEditSocket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class LiveEditRepositoryImpl @Inject constructor(
    private val liveEditSocket: LiveEditSocket
) : LiveEditRepository {

    override suspend fun connect() {
        liveEditSocket.connect()
        liveEditSocket.createKey()
    }

    override fun observeCardOperation(): Flow<CardOperation> {
        return liveEditSocket.observeCardOperations()
    }

    override fun observeSyncOperation(): Flow<SyncOperation> {
        return liveEditSocket.observeSyncOperations()
    }

    override fun observeDeckOperation(): Flow<DeckOperation> {
        return liveEditSocket.observeDeckOperations()
    }


    override suspend fun disconnect() {
        liveEditSocket.disconnect()
    }

    override fun getLiveEditState(): Flow<LiveEditState> {
        return liveEditSocket.isConnected.map {
            val key = liveEditSocket.key
            when {
                !it -> LiveEditState.Disabled(liveEditSocket.connectionError)
                it && key != null -> LiveEditState.Enabled(key)
                else -> LiveEditState.Connecting
            }
        }.distinctUntilChanged()
    }

    override fun getCurrentConnectionId(): String? {
        return liveEditSocket.key
    }

    override fun sendDecksAndCards(
        decks: List<Deck>,
        cards: List<Card>
    ) {
        liveEditSocket.sync(decks, cards)
    }

}