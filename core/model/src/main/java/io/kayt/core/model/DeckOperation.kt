package io.kayt.core.model

sealed interface DeckOperation {
    sealed interface DeckId {
        data class RemoteId(val id: String) : DeckId
        data class LocalId(val id: Long) : DeckId
    }

    data class Create(
        val remoteId: String,
        val name: String
    ) : DeckOperation

    data class Delete(
        val cardId: DeckId
    ) : DeckOperation

    data class Update(
        val cardId: DeckId,
        val name: String
    ) : DeckOperation
}