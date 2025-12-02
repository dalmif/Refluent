package io.kayt.core.model

sealed interface CardOperation {
    sealed interface CardId {
        data class RemoteId(val id: String) : CardId
        data class LocalId(val id: Long) : CardId
    }

    data class Create(
        val remoteId: String,
        val deckId: Long,
        val front: String,
        val back: String,
        val comment: String
    ) : CardOperation

    data class Delete(
        val cardId: CardId
    ) : CardOperation

    data class Update(
        val cardId: CardId,
        val front: String,
        val back: String,
        val comment: String
    ) : CardOperation
}