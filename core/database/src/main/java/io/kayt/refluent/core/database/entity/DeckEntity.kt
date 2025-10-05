package io.kayt.refluent.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val name: String,
    val color1: Int,
    val color2: Int,
    val createdDateTime: Long = System.currentTimeMillis()
)
