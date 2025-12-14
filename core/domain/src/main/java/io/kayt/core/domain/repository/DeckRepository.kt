package io.kayt.core.domain.repository

import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.core.model.ReviewMode
import io.kayt.core.model.SearchResultCard
import kotlinx.coroutines.flow.Flow

interface DeckRepository {

    suspend fun addNewDeck(name: String, colors: Pair<Int, Int>): Long

    suspend fun updateDeck(
        id: Long,
        name: String,
        colors: Pair<Int, Int>,
        reviewMode: ReviewMode? = null
    )

    suspend fun removeDeck(id: Long)

    suspend fun searchCardGlobally(query: String): List<SearchResultCard>

    suspend fun addNewCard(
        deckId: Long,
        frontSide: String,
        backSide: String,
        comment: String,
        phonetic: String
    ): Long

    suspend fun updateCard(
        cardId: Long,
        frontSide: String,
        backSide: String,
        comment: String,
        phonetic: String
    )

    suspend fun deleteCard(cardId: Long)

    fun getDeckById(deckId: Long): Flow<Deck>

    fun getAllDeck(): Flow<List<Deck>>

    fun getCardsForDeck(deckId: Long): Flow<List<Card>>

    fun getAllCards(): Flow<List<Card>>

    suspend fun getCardById(cardId: Long): Card?

    fun getDueCardsForDeck(deckId: Long): Flow<List<Card>>

    suspend fun saveReviewResult(card: Card, remembered: Boolean)
}
