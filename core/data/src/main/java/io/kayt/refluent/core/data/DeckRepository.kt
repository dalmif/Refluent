package io.kayt.refluent.core.data

import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.refluent.core.database.AppDatabase
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
    private val deckDataAccess by lazy { appDatabase.deckDao() }

    suspend fun addNewDeck(name: String, colors: Pair<Int, Int>) {
        withContext(Dispatchers.IO) {
            deckDataAccess.newDeck(
                DeckEntity(
                    name = name,
                    color1 = colors.first,
                    color2 = colors.second
                )
            )
        }
    }

    suspend fun addNewCard(
        deckId: Long,
        frontSide: String,
        backSide: String,
        comment: String
    ) {
        withContext(Dispatchers.IO) {
            deckDataAccess.insertCard(
                CardEntity(
                    deckOwnerId = deckId,
                    frontSide = frontSide,
                    backSide = backSide,
                    comment = comment,
                    phonetic = "",
                    isArchived = false,
                    tags = ""
                )
            )
        }
    }

    fun getDeckById(deckId: Long): Flow<Deck> = deckDataAccess
        .getDeckById(deckId)
        .map {
            it.let {
                Deck(
                    id = it.uid,
                    name = it.name,
                    colors = Pair(it.color1, it.color2),
                    totalCards = it.totalCards,
                    dueCards = it.dueCards
                )
            }
        }
        .flowOn(Dispatchers.IO)

    fun getAllDeck(): Flow<List<Deck>> = deckDataAccess
        .getDeckWithCardCounts()
        .map { list ->
            list.map {
                Deck(
                    id = it.uid,
                    name = it.name,
                    colors = Pair(it.color1, it.color2),
                    totalCards = it.totalCards,
                    dueCards = it.dueCards
                )
            }
        }
        .flowOn(Dispatchers.IO)

    fun getCardsForDeck(deckId: Long): Flow<List<Card>> = deckDataAccess
        .getCardsForDeck(deckId)
        .map { cards ->
            cards.map {
                Card(
                    id = it.uid,
                    front = it.frontSide,
                    back = it.backSide,
                    deckId = it.deckOwnerId,
                    interval = it.interval,
                    repetition = it.repetition,
                    easeFactor = it.easeFactor,
                    dueDate = it.nextReview,
                    lastReviewed = it.lastReviewed ?: 0L,
                    createdDateTime = it.createdDateTime,
                    isArchived = it.isArchived,
                    phonetic = it.phonetic,
                    comment = it.comment,
                    tags = it.tags
                )
            }
        }
}