package io.kayt.core.model

data class Card(
    val id: Long,
    val front: String,
    val back: String,
    val deckId: Long,
    val isArchived: Boolean,
    val phonetic: String,
    val comment: String,
    val tags: String,
    // This indicates that this card is not a real card, but it's made from a card to demonstrate a back first card
    val isVirtualBackCard : Boolean = false
)