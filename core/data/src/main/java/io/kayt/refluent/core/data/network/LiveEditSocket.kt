package io.kayt.refluent.core.data.network

import io.kayt.refluent.core.data.network.model.RegistrationRequestModel
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    suspend fun createKey() = withTimeout(REGISTRATION_TIMEOUT_MS) {
        if (!socket.connected()) return@withTimeout
        if (lastKey != null && key == null) {
            // It's disconnected, so it will reconnect by itself
            return@withTimeout
        }
        val id = randomId(length = 6)
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

    private fun randomId(length: Int): String {
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