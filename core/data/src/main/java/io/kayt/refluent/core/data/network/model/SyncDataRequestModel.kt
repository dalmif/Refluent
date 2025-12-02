package io.kayt.refluent.core.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeckDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("cardCount") val cardCount: Int,
    @SerialName("cardsDueForReview") val cardsDueForReview: Int,
    @SerialName("color1") val color1: Int,
    @SerialName("color2") val color2: Int,
)

@Serializable
data class CardDto(
    @SerialName("id") val id: Long,
    @SerialName("deckId") val deckId: Long,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("comment") val comment: String,
)

@Serializable
data class SyncDataRequestModel(
    val decks: List<DeckDto>,
    val cards: List<CardDto>
)