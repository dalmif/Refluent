package io.kayt.refluent.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "backside_cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["uid"],
            childColumns = ["deckOwnerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["uid"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("deckOwnerId")]
)
data class BackSideCardEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0L,
    val cardId: Long,
    val deckOwnerId: Long,
    val isArchived: Boolean,

    // SRS fields
    val nextReview: Long = System.currentTimeMillis(), // initially today
    val interval: Int = 1,
    val repetition: Int = 0,
    val easeFactor: Float = 2.5f,
    val lastReviewed: Long? = null,
)
