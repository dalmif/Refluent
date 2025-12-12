package io.kayt.refluent.core.database.entity

data class DeckWithStats(
    val uid: Long,
    val name: String,
    val color1: Int,
    val color2: Int,
    val totalCards: Int,
    val dueCards: Int,
    val backSideDueCards: Int,
    val nearestFrontReview: Long?,
    val nearestBackReview: Long?,
    val reviewMode: String?,
    // The cards that never reviewed from back side, will be considered as due for review
    // in Back-first, Shuffle-sides and Dual-sided review modes
    val neverReviewedBackCount: Int,
    val overlappingDuePairs: Int
)
