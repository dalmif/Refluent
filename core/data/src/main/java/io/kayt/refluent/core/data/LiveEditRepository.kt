package io.kayt.refluent.core.data

import io.kayt.core.model.LiveEditState
import io.kayt.refluent.core.data.network.LiveEditSocket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LiveEditRepository @Inject constructor(private val liveEditSocket: LiveEditSocket) {

    suspend fun connect() {
        liveEditSocket.connect()
        liveEditSocket.createKey()
    }

    suspend fun disconnect() {
        liveEditSocket.disconnect()
    }

    fun getLiveEditState(): Flow<LiveEditState> {
        return liveEditSocket.isConnected.map {
            val key = liveEditSocket.key
            when {
                !it -> LiveEditState.Disabled(liveEditSocket.connectionError)
                it && key != null -> LiveEditState.Enabled(key)
                else -> LiveEditState.Connecting
            }
        }.distinctUntilChanged()
    }

    fun getCurrentConnectionId(): String? {
        return liveEditSocket.key
    }

}