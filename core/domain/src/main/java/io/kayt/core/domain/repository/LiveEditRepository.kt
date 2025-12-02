package io.kayt.core.domain.repository

import io.kayt.core.model.Card
import io.kayt.core.model.CardOperation
import io.kayt.core.model.Deck
import io.kayt.core.model.LiveEditState
import io.kayt.core.model.SyncOperation
import kotlinx.coroutines.flow.Flow

interface LiveEditRepository {
    suspend fun connect()

    fun observeCardOperation(): Flow<CardOperation>

    fun observeSyncOperation(): Flow<SyncOperation>

    suspend fun disconnect()

    fun getLiveEditState(): Flow<LiveEditState>

    fun getCurrentConnectionId(): String?

    fun sendDecksAndCards(decks : List<Deck>, cards : List<Card>)
}