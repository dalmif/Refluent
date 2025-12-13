package io.kayt.refluent.core.data.network

import io.kayt.core.model.Card
import io.kayt.core.model.CardOperation
import io.kayt.core.model.Deck
import io.kayt.core.model.DeckOperation
import io.kayt.core.model.SyncOperation
import io.kayt.refluent.core.data.network.model.CardDto
import io.kayt.refluent.core.data.network.model.CardOperationsDto
import io.kayt.refluent.core.data.network.model.DeckDto
import io.kayt.refluent.core.data.network.model.DeckOperationsDto
import io.kayt.refluent.core.data.network.model.RegistrationRequestModel
import io.kayt.refluent.core.data.network.model.SyncDataRequestModel
import io.kayt.refluent.core.data.network.model.SyncOperationsDto
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.json.JSONObject
import javax.inject.Inject

class LiveEditSocket @Inject constructor(private val socket: Socket) {


    private val _isConnected = MutableSharedFlow<Boolean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).also { it.tryEmit(false) }

    val isConnected = _isConnected.asSharedFlow()
    var key: String? = null
        private set

    // Store the last key that is used to register again with the same key when reconnect
    private var lastKey: String? = null
    var connectionError: String? = null
        private set

    companion object {
        const val REGISTRATION_TIMEOUT_MS = 30_000L
    }

    init {
        socket.on(Socket.EVENT_CONNECT) {
            connectionError = null
            _isConnected.tryEmit(true)
            // It's reconnecting after a disconnection, so we need to register again
            val lastkey = lastKey
            if (lastkey != null && key == null) {
                CoroutineScope(Job()).launch {
                    register(lastkey)
                }
            }
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            // Show the error only if it's the first time we are trying to connect
            if (key == null && lastKey == null) {
                connectionError = "An error occurred, please check your " +
                        "internet connection and try again"
                _isConnected.tryEmit(false)
                socket.disconnect()
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) { args ->
            connectionError = null
            lastKey = key
            key = null
            _isConnected.tryEmit(socket.isActive)
        }
    }

    fun sync(
        decks: List<Deck>,
        cards: List<Card>,
        deckRemoteIds: List<Pair<String, Long>>,
        cardRemoteIds: List<Pair<String, Long>>,
    ) {
        val deckRemoteIdMap = deckRemoteIds.associate { it.second to it.first }
        val cardRemoteIdsMap = cardRemoteIds.associate { it.second to it.first }
        socket.emit(
            "sync:response",
            Json.encodeToString(
                SyncDataRequestModel(
                    decks = decks.map {
                        DeckDto(
                            id = it.id,
                            title = it.name,
                            cardCount = it.totalCards,
                            cardsDueForReview = it.dueCards,
                            color1 = it.colors.first,
                            color2 = it.colors.second,
                            remoteId = deckRemoteIdMap[it.id]
                        )
                    },
                    cards = cards.map {
                        CardDto(
                            id = it.id,
                            deckId = it.deckId,
                            front = it.front,
                            back = it.back,
                            comment = it.comment,
                            remoteId = cardRemoteIdsMap[it.id]
                        )
                    }
                )
            ).toJsonObject()
        )
    }

    fun observeCardOperations(): Flow<CardOperation> = collectFromSocket(
        events = CardOperationsDto.entries,
        keySelector = { it.value }
    ) { event, objects ->
        val json = (objects.first() as JSONObject)
        when (event) {
            CardOperationsDto.Create -> CardOperation.Create(
                remoteId = json.getString("cardId"),
                deckId = json.getString("deckId").toLongOrNull()
                    ?.let { DeckOperation.DeckId.LocalId(it) }
                    ?: DeckOperation.DeckId.RemoteId(json.getString("deckId")),
                front = json.getString("front"),
                back = json.getString("back"),
                comment = json.getString("comment")
            )

            CardOperationsDto.Update -> CardOperation.Update(
                cardId = json.getString("cardId")
                    .toLongOrNull()?.let { CardOperation.CardId.LocalId(it) }
                    ?: CardOperation.CardId.RemoteId(json.getString("cardId")),
                front = json.getString("front"),
                back = json.getString("back"),
                comment = json.getString("comment")
            )

            CardOperationsDto.Delete -> CardOperation.Delete(
                json.getString("cardId")
                    .toLongOrNull()?.let { CardOperation.CardId.LocalId(it) }
                    ?: CardOperation.CardId.RemoteId(json.getString("cardId")))
        }
    }

    fun observeSyncOperations(): Flow<SyncOperation> = collectFromSocket(
        events = SyncOperationsDto.entries,
        keySelector = { it.value },
    ) { event, objects ->
        val json = (objects.first() as JSONObject)
        when (event) {
            SyncOperationsDto.SyncRequested -> SyncOperation.SyncRequested
            SyncOperationsDto.PeerConnected -> SyncOperation.PeerConnected(
                type = json.getString("type")
            )
        }
    }

    fun observeDeckOperations(): Flow<DeckOperation> = collectFromSocket(
        events = DeckOperationsDto.entries,
        keySelector = { it.value },
    ) { event, objects ->
        val json = (objects.first() as JSONObject)
        return@collectFromSocket when (event) {
            DeckOperationsDto.Create -> DeckOperation.Create(
                remoteId = json.getString("deckId"),
                name = json.getString("title")
            )

            else -> error("Not supported yet")
        }
    }

    private fun <R : Any, E : Any> collectFromSocket(
        events: List<E>,
        keySelector: (E) -> String,
        mapper: (E, Array<Any>) -> R
    ): Flow<R> = callbackFlow {
        val registered = events.map { event ->
            Emitter.Listener { trySend(mapper(event, it)) }
                .also { socket.on(keySelector(event), it) }
                .let { keySelector(event) to it }
        }
        // unregister the listeners when the flow is closed
        awaitClose {
            registered.forEach { (event, listener) ->
                socket.off(event, listener)
            }
        }
    }


    suspend fun connect() {
        // Emit this to put the state on loading
        if (!socket.connected()) {
            connectionError = null
            lastKey = null
            key = null
            _isConnected.tryEmit(true)
            socket.connect()
            // wait until we get a signal that we are connected or error
            val isConnected = _isConnected.drop(1).first()
            // disconnect explicitly to stop retrying
            if (!isConnected) socket.disconnect()
        }
    }

    suspend fun disconnect() {
        if (socket.connected()) {
            lastKey = null
            key = null
            socket.disconnect()
        }
        // wait until we get a signal that we are connected
        _isConnected.first { !it }
    }

    suspend fun registerWithTimeout(id: String) = withTimeout(REGISTRATION_TIMEOUT_MS) {
        if (!socket.connected()) return@withTimeout
        if (lastKey != null && key == null) {
            // It's disconnected, so it will reconnect by itself
            return@withTimeout
        }
        register(id)
        id
    }


    private suspend fun register(key: String) = suspendCancellableCoroutine { conn ->
        val registeredOnCallback = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                if (socket.connected()) {
                    this@LiveEditSocket.key = key
                    // Emit "connected" again so listener can check for the key
                    _isConnected.tryEmit(true)
                }
                conn.resumeWith(Result.success(key))
                socket.off("registered", this)
            }
        }
        socket.on("registered", registeredOnCallback).emit(
            "register",
            Json.encodeToString(
                RegistrationRequestModel(
                    phoneId = key,
                    type = "android"
                )
            ).toJsonObject()
        )
        conn.invokeOnCancellation {
            socket.off("registered", registeredOnCallback)
        }
    }

    fun createKey(length: Int): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString {
            repeat(length) {
                append(chars.random())
            }
        }
    }

    private fun String.toJsonObject(): JSONObject {
        return JSONObject(this)
    }
}