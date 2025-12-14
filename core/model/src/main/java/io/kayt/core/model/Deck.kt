package io.kayt.core.model

data class Deck(
    val id : Long,
    val name : String,
    val colors: Pair<Int, Int>,
    val totalCards: Int,
    val dueCards: Int,
    val reviewMode : ReviewMode
)