package me.tomasan7.chatappserver.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import me.tomasan7.chatappserver.Message
import me.tomasan7.chatappserver.Storage
import me.tomasan7.chatappserver.getUsername
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun Application.configureSockets()
{
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/messages-live") { // websocketSession
            val username = call.getUsername() ?: return@webSocket close(CloseReason(4000, "Missing username"))
            val connection = Connection(username, this)
            Storage.connections[username] = connection
            println("$username connected")

            try
            {
                for (frame in incoming)
                {
                    if (frame is Frame.Text)
                    {
                        val message = Message(username, frame.readText(), dateTimeFormatter.format(
                            LocalTime.now()))
                        Storage.messages.add(message)
                        Storage.connections.values.forEach { it.session.sendSerialized(message) }
                    }
                }
            }
            catch (e: ClosedReceiveChannelException)
            {
                println("onClose ${closeReason.await()}")
                Storage.connections.remove(username)
            }
            catch (e: Throwable)
            {
                println("onError ${closeReason.await()}")
                Storage.connections.remove(username)
                e.printStackTrace()
            }
        }
    }
}

data class Connection(val username: String, val session: DefaultWebSocketServerSession)
