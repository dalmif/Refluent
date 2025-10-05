@file:Suppress("MaxLineLength")

package io.kayt.refluent.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.CardWithDeck
import io.kayt.refluent.core.database.entity.DeckEntity
import io.kayt.refluent.core.database.entity.DeckWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE uid = :deckId")
    suspend fun readDeckById(deckId: Long): DeckEntity

    @Insert
    suspend fun newDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Delete
    suspend fun delete(deck: DeckEntity)

    @Query("DELETE FROM decks WHERE uid = :deckId")
    suspend fun deleteDeckById(deckId: Long)

    @Query("DELETE FROM cards WHERE deckOwnerId = :deckId")
    suspend fun deleteCardsByDeckId(deckId: Long)

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE uid = :cardId")
    suspend fun deleteCardById(cardId: Long)

    @Query("SELECT * FROM cards WHERE deckOwnerId = :deckId ORDER BY uid DESC")
    fun getCardsForDeck(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE uid = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity?

    @Query(
        "SELECT d.uid, d.name, d.color1, d.color2, COUNT(c.uid) AS totalCards, " +
            "SUM(CASE WHEN c.nextReview IS NOT NULL AND c.nextReview <= (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000 THEN 1 ELSE 0 END) AS dueCards " +
            "FROM decks AS d LEFT JOIN cards AS c ON c.deckOwnerId = d.uid GROUP BY d.uid, d.name ORDER BY d.createdDateTime ASC;"
    )
    fun getDeckWithCardCounts(): Flow<List<DeckWithStats>>

    @Query(
        """
        SELECT * FROM cards
        WHERE nextReview > CAST(strftime('%s','now') AS INTEGER) * 1000
        ORDER BY nextReview ASC
        LIMIT 1
       """
    )
    fun getTheNearestDueCard(): Flow<CardEntity?>

    @Query(
        """
        SELECT d.uid, d.name, d.color1, d.color2,
           COUNT(c.uid) AS totalCards,
           SUM(
             CASE
               WHEN c.nextReview IS NOT NULL
                AND c.nextReview <= (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000
               THEN 1 ELSE 0
             END
           ) AS dueCards,
           (
             SELECT c2.nextReview
             FROM cards AS c2
             WHERE c2.deckOwnerId = d.uid
               AND c2.nextReview IS NOT NULL
               AND c2.nextReview > (CAST(strftime('%s','now') AS INTEGER) * 1000)
             ORDER BY c2.nextReview ASC
             LIMIT 1
           ) AS nearestNextReview
        FROM decks AS d
        LEFT JOIN cards AS c ON c.deckOwnerId = d.uid
        WHERE d.uid = :deckId
        GROUP BY d.uid, d.name, d.color1, d.color2
        ORDER BY d.createdDateTime DESC
        LIMIT 1
"""
    )
    fun getDeckById(deckId: Long): Flow<DeckWithStats>

    @Transaction
    @Query(
        """
        SELECT * FROM cards
        WHERE frontSide LIKE '%' || :query || '%' COLLATE NOCASE
           OR backSide  LIKE '%' || :query || '%' COLLATE NOCASE
           OR comment   LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY
            CASE
                WHEN frontSide LIKE '%' || :query || '%' COLLATE NOCASE THEN 1
                WHEN backSide  LIKE '%' || :query || '%' COLLATE NOCASE THEN 2
                WHEN comment   LIKE '%' || :query || '%' COLLATE NOCASE THEN 3
                ELSE 4
            END,
            createdDateTime DESC
    """
    )
    suspend fun searchCardsWithDeck(query: String): List<CardWithDeck>
}
