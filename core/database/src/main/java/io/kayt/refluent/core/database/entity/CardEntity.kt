package io.kayt.refluent.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["uid"],
            childColumns = ["deckOwnerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("deckOwnerId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0L,
    val deckOwnerId: Long,
    val frontSide: String,
    val backSide: String,
    val phonetic: String,
    val comment: String,
    val isArchived: Boolean,
    val tags: String,

    // SRS fields
    val nextReview: Long = System.currentTimeMillis(), // initially today
    val interval: Int = 1,
    val repetition: Int = 0,
    val easeFactor: Float = 2.5f,
    val lastReviewed: Long? = null,

    val createdDateTime: Long = System.currentTimeMillis()
) {
    // Just a simple marker to make it easier to distinguish between real cards and virtual cards
    // Virtual cards are cards that are made from a real card to demonstrate a back first card
    // They don't have their own id and they don't get inserted as cards but backSideCards
    @Ignore
    var isVirtual: Boolean = false
}
