package io.kayt.refluent.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val name: String,
    val color1: Int,
    val color2: Int,
    val createdDateTime: Long = System.currentTimeMillis(),
    // They are added in version 2 so we keep them nullable for ease of migration
    val fromLang: String?,
    val toLang: String?,
    val reviewMode: String?
)
