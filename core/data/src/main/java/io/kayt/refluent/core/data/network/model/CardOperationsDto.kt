package io.kayt.refluent.core.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CardOperationsDto(val value: String) {
    Create("card:create"),
    Update("card:update"),
    Delete("card:delete")
}

@Serializable
data class CardAddDto(
    @SerialName("deckId") val deckId: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("comment") val comment: String
)