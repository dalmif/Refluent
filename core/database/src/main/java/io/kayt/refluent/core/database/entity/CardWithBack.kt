package io.kayt.refluent.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

class CardWithBack (
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "cardId"
    )
    val back: BackSideCardEntity?
)