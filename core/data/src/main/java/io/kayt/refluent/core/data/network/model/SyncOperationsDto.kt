package io.kayt.refluent.core.data.network.model

enum class SyncOperationsDto(val value: String) {
    PeerConnected("peer-connected"),
    SyncRequested("sync:request")
}