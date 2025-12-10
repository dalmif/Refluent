package io.kayt.core.model

data class Card(
    val id: Long,
    val front: String,
    val back: String,
    val deckId: Long,
    val interval: Int,
    val repetition: Int,
    val easeFactor: Float,
    val dueDate: Long,
    val lastReviewed: Long,
    val createdDateTime: Long,
    val isArchived: Boolean,
    val phonetic: String,
    val comment: String,
    val tags: String,
    val backSideReview : BackSideCardReview?
)

data class BackSideCardReview(
    val interval: Int,
    val repetition: Int,
    val easeFactor: Float,
)