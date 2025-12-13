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
    val interval: Int = DEFAULT_INTERVAL,
    val repetition: Int = DEFAULT_REPETITION,
    val easeFactor: Float = DEFAULT_EASE_FACTOR,
    val lastReviewed: Long? = null,
)

const val DEFAULT_EASE_FACTOR = 2.5f
const val DEFAULT_REPETITION = 0
const val DEFAULT_INTERVAL = 1