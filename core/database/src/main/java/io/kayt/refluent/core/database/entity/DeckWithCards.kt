package io.kayt.refluent.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "deckOwnerId"
    )
    val cards: List<CardEntity>
)
