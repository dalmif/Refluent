package io.kayt.refluent.core.database.dictionary

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Query

@Entity(
    tableName = "cmudict",
    primaryKeys = ["word", "variant"],
    indices = [
        Index(value = ["word"], name = "idx_cmudict_word_ci", unique = false) // Add this
    ]
)
data class Entry(
    @ColumnInfo(name = "word")
    val word: String,
    @ColumnInfo(name = "variant", defaultValue = "0")
    val variant: Int,
    @ColumnInfo(name = "arpabet")
    val arpabet: String
)

@Dao
interface CmuDao {
    @Query("SELECT * FROM cmudict WHERE word = :w COLLATE NOCASE ORDER BY variant")
    suspend fun byWord(w: String): List<Entry>

    @Query("SELECT word FROM cmudict WHERE word LIKE :prefix || '%' LIMIT :limit")
    suspend fun prefix(prefix: String, limit: Int = 20): List<String>
}