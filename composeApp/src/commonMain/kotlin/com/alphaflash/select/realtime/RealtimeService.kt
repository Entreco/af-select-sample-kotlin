package com.alphaflash.select.realtime

import com.alphaflash.select.realtime.StompMessage.Companion.DELIMITER
import com.alphaflash.select.realtime.StompMessage.Companion.NULL
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.wss
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

const val REALTIME_HOST = "select.alphaflash.com"
const val REALTIME_PORT = 443

interface RealtimeService {
    val heartBeats: Flow<Instant>
    fun connect(passcode: String, destination: String): Flow<Observation>
    fun disconnect()
}

internal class RealtimeServiceImpl(
    private val client: HttpClient
) : RealtimeService {
    private val _beats: MutableStateFlow<Instant> = MutableStateFlow(Clock.System.now())

    override val heartBeats: Flow<Instant> = _beats

    override fun connect(passcode: String, destination: String): Flow<Observation> = callbackFlow {
        client.wss(HttpMethod.Get, REALTIME_HOST, REALTIME_PORT) {
            val connect = StompMessage("CONNECT").apply {
                addHeader("passcode" to passcode)
                addHeader("heart-beat" to "0,30000")
            }

            outgoing.send(connect.toFrame())
            val response = incoming.receive().toStompMessage()
            if (response.messageType != "CONNECTED") cancel("Unexpected response: ${response.messageType}")
            val subscribe = StompMessage("SUBSCRIBE").apply {
                addHeader("destination" to destination)
            }
            outgoing.send(subscribe.toFrame())
            while (isActive) {
                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        val stompMessage = frame.toStompMessage()
                        println(Clock.System.now().toString() + ": " + stompMessage)
                        when(stompMessage.messageType){
                            "HEARTBEAT" -> _beats.value = Clock.System.now()
                            "MESSAGE" -> stompMessage.handleMessage(destination){ trySend(it) }
                        }
                    }
                    else -> cancel("Unexpected frame: $frame")
                }
            }

            awaitClose {
                println("Closing connection")
                close()
            }
        }
    }

    private fun StompMessage.handleMessage(destination: String, onAccepted: (Observation)->Unit){
        if (firstHeader("destination") == destination) {
            val observations = Json.decodeFromString<Collection<Observation>>(body)
            observations.forEach { onAccepted(it) }
        }
    }

    override fun disconnect() {
        client.cancel("User initiated disconnect")
        client.close()
    }
}

private fun StompMessage.toFrame(): Frame.Text = Frame.Text(buildString {
    append(messageType)
    append(DELIMITER)

    headers().map { entries ->
        entries.value.map { value ->
            append(entries.key)
            append(":")
            append(value)
            append(DELIMITER)
        }
    }

    append(DELIMITER)
    append(body)
    append(NULL)
})

private fun Frame.toStompMessage(): StompMessage = try {
    val message = (this as Frame.Text).readText()
    val parts = message.split(DELIMITER)
    if (parts.first() == "") throw ClassCastException("Wrong Type")
    val messageType = parts.first()
    val messageBody = parts.last()
    StompMessage(messageType, messageBody).apply {
        parts.drop(1).dropLast(1).forEach { header ->
            if (header.isEmpty()) return@forEach
            val (key, value) = header.split(":")
            addHeader(key to value)
        }
    }
} catch (oops: ClassCastException) {
    StompMessage("HEARTBEAT")
}