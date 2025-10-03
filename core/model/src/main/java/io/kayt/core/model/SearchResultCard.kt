package io.kayt.core.model

data class SearchResultCard(
    val card: Card,
    val deckColor : Pair<Int, Int>,
    val deckName : String,
)