package io.kayt.core.model

sealed interface SyncOperation {
    data object SyncRequested : SyncOperation
    data class PeerConnected(val type: String) : SyncOperation
}