package io.kayt.refluent.core.data

import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.model.Card
import io.kayt.core.model.CardOperation
import io.kayt.core.model.Config
import io.kayt.core.model.Deck
import io.kayt.core.model.DeckOperation
import io.kayt.core.model.LiveEditState
import io.kayt.core.model.SyncOperation
import io.kayt.refluent.core.data.network.LiveEditSocket
import io.kayt.refluent.core.data.storage.UserStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class LiveEditRepositoryImpl @Inject constructor(
    private val liveEditSocket: LiveEditSocket,
    private val userStorage: UserStorage,
    private val config: Config
) : LiveEditRepository {

    override suspend fun connect(): Result<Unit> {
        liveEditSocket.connect(config.versionCode)
        val key = userStorage.getLastLiveEditKey() ?: liveEditSocket.createKey(6)
        try {
            liveEditSocket.registerWithTimeout(key).also {
                userStorage.saveLiveEditKey(key)
            }
        } catch (e: LiveEditSocket.AppUpdateRequiredException) {
            return Result.failure(e)
        }
        return Result.success(Unit)
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
        userStorage.clearLiveEditKey()
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
        cards: List<Card>,
        deckRemoteIds: List<Pair<String, Long>>,
        cardRemoteIds: List<Pair<String, Long>>,
    ) {
        liveEditSocket.sync(decks, cards, deckRemoteIds, cardRemoteIds)
    }

}