package me.tomasan7.chatappserver.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tomasan7.chatappserver.ChatAppSession
import me.tomasan7.chatappserver.Message
import me.tomasan7.chatappserver.Storage
import org.slf4j.LoggerFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
private val chatAppSessionLogger = LoggerFactory.getLogger(ChatAppSession::class.java)

fun ApplicationCall.getUsername() = request.cookies["username"]

fun Application.configureRouting()
{
    routing {

        get("/") {
            call.respondRedirect("/chatapp/")
        }

        route("chatapp") {
            staticResources("/static", "static")
            staticResources("/", "pages")

            get("/") {
                val username = call.getUsername() ?: return@get call.respondRedirect("/chatapp/welcome.html")

                val model = mapOf(
                    "messages" to Storage.messages,
                    "username" to username,
                    "onlineUsers" to Storage.connections.keys.filter { it != username }
                )

                call.respondTemplate("chatapp.ftlh", model)
            }

            post("/login") {
                val formParams = call.receiveParameters()
                val username = formParams["username"] ?: return@post call.respondText(
                    "Missing username",
                    status = HttpStatusCode.BadRequest
                )

                call.response.cookies.append(Cookie("username", username))
                call.respondRedirect("/chatapp/")
            }

            webSocket("/messages-live") {
                val username = call.getUsername() ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Missing username")
                )
                val chatappSession = ChatAppSession(username, this)
                Storage.connections[username] = chatappSession
                chatAppSessionLogger.info("$username connected")
                Storage.connections.filterKeys { it != username }.values.forEach {
                    it.session.send(constructJoinWsMessage(username))
                }

                suspend fun disconnect()
                {
                    chatAppSessionLogger.info("$username disconnected (${closeReason.await()})")
                    Storage.connections.filterKeys { it != username }.values.forEach {
                        it.session.send(constructLeaveWsMessage(username))
                    }
                    Storage.connections.remove(username)
                }

                try
                {
                    for (frame in incoming)
                    {
                        if (frame !is Frame.Text)
                            continue

                        val message = Message(username, frame.readText(), TIME_FORMATTER.format(LocalTime.now()))
                        Storage.messages.add(message)
                        Storage.connections.values.forEach { it.session.send(constructMessageWsMessage(message)) }
                    }

                    disconnect()
                }
                catch (e: ClosedReceiveChannelException)
                {
                    disconnect()
                }
                catch (e: Throwable)
                {
                    chatAppSessionLogger.error(e)
                    Storage.connections.remove(username)
                }
            }
        }
    }
}

fun constructJoinWsMessage(username: String) = "JOIN\n$username"

fun constructLeaveWsMessage(username: String) = "LEAVE\n$username"

fun constructMessageWsMessage(message: Message) = "MESSAGE\n${Json.encodeToString(message)}"
