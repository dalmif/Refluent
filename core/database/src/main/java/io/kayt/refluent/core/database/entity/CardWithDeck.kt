package io.kayt.refluent.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CardWithDeck(
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "deckOwnerId",
        entityColumn = "uid"
    )
    val deck: DeckEntity
)