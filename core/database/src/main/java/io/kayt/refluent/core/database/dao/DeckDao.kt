package io.kayt.refluent.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity
import io.kayt.refluent.core.database.entity.DeckWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE uid = :deckId")
    suspend fun findByName(deckId: Int): DeckEntity

    @Insert
    suspend fun newDeck(deck: DeckEntity): Long

    @Delete
    suspend fun delete(user: DeckEntity)

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT * FROM cards WHERE deckOwnerId = :deckId")
    fun getCardsForDeck(deckId: Long): Flow<List<CardEntity>>

    @Query(
        "SELECT d.uid, d.name, d.color1, d.color2, COUNT(c.uid) AS totalCards, " +
                "SUM(CASE WHEN c.nextReview IS NOT NULL AND c.nextReview <= :nowTs THEN 1 ELSE 0 END) AS dueCards " +
                "FROM decks AS d LEFT JOIN cards AS c ON c.deckOwnerId = d.uid GROUP BY d.uid, d.name ORDER BY d.createdDateTime DESC;"
    )
    fun getDeckWithCardCounts(nowTs: Long = System.currentTimeMillis()): Flow<List<DeckWithStats>>
}