package io.kayt.refluent.core.database.entity

data class DeckWithStats(
    val deckId: Long,
    val name: String,
    val totalCards: Int,
    val dueCards: Int
)