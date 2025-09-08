package io.kayt.refluent.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity
import io.kayt.refluent.core.database.entity.DeckWithStats

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    fun getAll(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE uid = :deckId")
    fun findByName(deckId: Int): DeckEntity

    @Insert
    fun newDeck(deck: DeckEntity): Long

    @Delete
    fun delete(user: DeckEntity)

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT * FROM cards WHERE deckOwnerId = :deckId")
    suspend fun getCardsForDeck(deckId: Long): List<CardEntity>

    @Query(
        "SELECT d.uid, d.name, COUNT(c.uid) AS totalCards, " +
                "SUM(CASE WHEN c.nextReview IS NOT NULL AND c.nextReview <= :nowTs THEN 1 ELSE 0 END) AS dueCards " +
                "FROM decks AS d LEFT JOIN cards AS c ON c.deckOwnerId = d.uid GROUP BY d.uid, d.name ORDER BY d.createdDateTime DESC;"
    )
    suspend fun getDeckWithCardCounts(nowTs: Long = System.currentTimeMillis()): List<DeckWithStats>
}