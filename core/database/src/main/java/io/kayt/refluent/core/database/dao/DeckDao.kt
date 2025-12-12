@file:Suppress("MaxLineLength")

package io.kayt.refluent.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.kayt.refluent.core.database.entity.BackSideCardEntity
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.CardWithBack
import io.kayt.refluent.core.database.entity.CardWithDeck
import io.kayt.refluent.core.database.entity.DeckEntity
import io.kayt.refluent.core.database.entity.DeckWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE uid = :deckId")
    suspend fun getDeckById(deckId: Long): DeckEntity

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
    suspend fun insertCard(card: CardEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackSideCard(card: BackSideCardEntity) : Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE uid = :cardId")
    suspend fun deleteCardById(cardId: Long)

    @Query("SELECT * FROM cards WHERE deckOwnerId = :deckId ORDER BY uid DESC")
    fun getCardsForDeck(deckId: Long): Flow<List<CardWithBack>>

    @Query("SELECT * FROM cards ORDER BY uid DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE uid = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity?

    @Query("SELECT * FROM backside_cards WHERE cardId = :cardId")
    suspend fun getBackSideCardById(cardId: Long): BackSideCardEntity?

    @Query(
//        "SELECT d.uid, d.name, d.reviewMode, d.color1, d.color2, COUNT(c.uid) AS totalCards, " +
//                "SUM(CASE WHEN c.nextReview IS NOT NULL AND c.nextReview <= (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000 THEN 1 ELSE 0 END) AS dueCards, " +
//                "SUM(CASE WHEN bc.nextReview IS NOT NULL AND bc.nextReview <= (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000 THEN 1 ELSE 0 END) AS backSideDueCards " +
//                "FROM decks AS d LEFT JOIN cards AS c ON c.deckOwnerId = d.uid LEFT JOIN backside_cards AS bc ON bc.deckOwnerId = d.uid " +
//                "GROUP BY d.uid, d.name ORDER BY d.createdDateTime ASC;"

        """
            WITH nowtable AS (
                SELECT (CAST(strftime('%s','now') AS INTEGER) * 1000) AS nowMs
            )
            SELECT 
                d.uid,
                d.name,
                d.color1,
                d.color2,
                d.reviewMode,
            
                COUNT(c.uid) AS totalCards,
            
                -- FRONT DUE
                SUM(
                    CASE 
                        WHEN c.nextReview IS NOT NULL AND c.nextReview <= nowMs 
                        THEN 1 ELSE 0 
                    END
                ) AS dueCards,
            
                -- BACK DUE
                SUM(
                    CASE 
                        WHEN bc.nextReview IS NOT NULL AND bc.nextReview <= nowMs 
                        THEN 1 ELSE 0 
                    END
                ) AS backSideDueCards,
            
                -- BACKSIDE DOES NOT EXIST (never reviewed)
                SUM(
                    CASE 
                        WHEN bc.uid IS NULL THEN 1 ELSE 0 
                    END
                ) AS neverReviewedBackCount,
            
                -- OVERLAPS FOR SHUFFLE MODE
                SUM(
                    CASE
                        -- Case 1: backside exists AND both due
                        WHEN bc.uid IS NOT NULL
                             AND c.nextReview IS NOT NULL AND c.nextReview <= nowMs
                             AND bc.nextReview IS NOT NULL AND bc.nextReview <= nowMs
                        THEN 1
            
                        -- Case 2: backside missing AND front due
                        WHEN bc.uid IS NULL
                             AND c.nextReview IS NOT NULL AND c.nextReview <= nowMs
                        THEN 1
            
                        ELSE 0
                    END
                ) AS overlappingDuePairs,
            
                -- NEAREST FRONT REVIEW
                MIN(
                    CASE WHEN c.nextReview IS NOT NULL AND c.nextReview > nowMs
                         THEN c.nextReview END
                ) AS nearestFrontReview,
            
                -- NEAREST BACK REVIEW
                MIN(
                    CASE WHEN bc.nextReview IS NOT NULL AND bc.nextReview > nowMs
                         THEN bc.nextReview END
                ) AS nearestBackReview
            
            FROM decks d
            JOIN nowtable
            LEFT JOIN cards c ON c.deckOwnerId = d.uid
            LEFT JOIN backside_cards bc ON bc.cardId = c.uid
            GROUP BY d.uid, d.name
            ORDER BY d.createdDateTime ASC
            """
    )
    fun getDeckWithCardCounts(): Flow<List<DeckWithStats>>

    @Query(
        """
            WITH nowtable AS (
                SELECT (CAST(strftime('%s','now') AS INTEGER) * 1000) AS nowMs
            )
            SELECT MIN(nextReview) 
            FROM (
                SELECT c.nextReview AS nextReview
                FROM cards c, nowtable
                WHERE c.nextReview > nowMs
        
                UNION ALL
        
                SELECT bc.nextReview AS nextReview
                FROM backside_cards bc, nowtable
                WHERE bc.nextReview > nowMs
            )
        """
    )
    fun getTheNearestDueCard(): Flow<Long?>

    @Query(
//        """
//        SELECT d.uid, d.name, d.color1, d.color2, d.reviewMode,
//           COUNT(c.uid) AS totalCards,
//           SUM(
//             CASE
//               WHEN c.nextReview IS NOT NULL
//                AND c.nextReview <= (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000
//               THEN 1 ELSE 0
//             END
//           ) AS dueCards,
//           (
//             SELECT COUNT(*)
//             FROM backside_cards AS bc2
//             WHERE bc2.deckOwnerId = d.uid
//               AND bc2.nextReview IS NOT NULL
//               AND bc2.nextReview > (CAST(strftime('%s','now') AS INTEGER) * 1000) + 1000
//           ) AS backSideDueCards,
//           MIN((
//             SELECT c2.nextReview
//             FROM cards AS c2
//             WHERE c2.deckOwnerId = d.uid
//               AND c2.nextReview IS NOT NULL
//               AND c2.nextReview > (CAST(strftime('%s','now') AS INTEGER) * 1000)
//             ORDER BY c2.nextReview ASC
//             LIMIT 1
//           ),
//           (
//             SELECT bc2.nextReview
//             FROM backside_cards AS bc2
//             WHERE bc2.deckOwnerId = d.uid
//               AND bc2.nextReview IS NOT NULL
//               AND bc2.nextReview > (CAST(strftime('%s','now') AS INTEGER) * 1000)
//             ORDER BY bc2.nextReview ASC
//             LIMIT 1
//           ))  AS nearestNextReview
//        FROM decks AS d
//        LEFT JOIN cards AS c ON c.deckOwnerId = d.uid
//        WHERE d.uid = :deckId
//        GROUP BY d.uid, d.name, d.color1, d.color2
//        ORDER BY d.createdDateTime DESC
//        LIMIT 1
//"""
        """
            WITH nowtable AS (
                SELECT (CAST(strftime('%s','now') AS INTEGER) * 1000) AS nowMs
            )
            SELECT 
                d.uid,
                d.name,
                d.color1,
                d.color2,
                d.reviewMode,
            
                COUNT(c.uid) AS totalCards,
            
                SUM(
                    CASE 
                        WHEN c.nextReview IS NOT NULL AND c.nextReview <= nowMs 
                        THEN 1 ELSE 0 
                    END
                ) AS dueCards,
            
                SUM(
                    CASE 
                        WHEN bc.nextReview IS NOT NULL AND bc.nextReview <= nowMs 
                        THEN 1 ELSE 0 
                    END
                ) AS backSideDueCards,
            
                SUM(
                    CASE 
                        WHEN bc.uid IS NULL THEN 1 ELSE 0 
                    END
                ) AS neverReviewedBackCount,
            
                SUM(
                    CASE
                        -- CASE 1: backside exists AND both front and back are due
                        WHEN bc.uid IS NOT NULL 
                             AND c.nextReview IS NOT NULL 
                             AND c.nextReview <= nowMs
                             AND bc.nextReview IS NOT NULL
                             AND bc.nextReview <= nowMs
                        THEN 1
            
                        -- CASE 2: backside missing → backside implicitly due
                        WHEN bc.uid IS NULL 
                             AND c.nextReview IS NOT NULL
                             AND c.nextReview <= nowMs
                        THEN 1
            
                        ELSE 0
                    END
                ) AS overlappingDuePairs,
            
                MIN(
                    CASE WHEN c.nextReview IS NOT NULL AND c.nextReview > nowMs
                    THEN c.nextReview END
                ) AS nearestFrontReview,
            
                MIN(
                    CASE WHEN bc.nextReview IS NOT NULL AND bc.nextReview > nowMs
                    THEN bc.nextReview END
                ) AS nearestBackReview
            
            FROM decks d
            JOIN nowtable
            LEFT JOIN cards c ON c.deckOwnerId = d.uid
            LEFT JOIN backside_cards bc ON bc.cardId = c.uid
            WHERE d.uid = :deckId
            GROUP BY d.uid
            LIMIT 1
        """
    )
    fun getDeckWithStatsById(deckId: Long): Flow<DeckWithStats>

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
