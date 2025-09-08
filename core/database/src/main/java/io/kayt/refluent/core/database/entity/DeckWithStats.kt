package io.kayt.refluent.core.database.entity

data class DeckWithStats(
    val deckId: Long,
    val name: String,
    val color1: Int,
    val color2: Int,
    val totalCards: Int,
    val dueCards: Int
)