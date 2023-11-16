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
import me.tomasan7.chatappserver.Message
import me.tomasan7.chatappserver.Storage
import me.tomasan7.chatappserver.getUsername
import java.io.File

fun Application.configureRouting()
{
    routing {
        staticResources("/static", "static")

        get("/") {
            val username = call.getUsername() ?: return@get call.respondRedirect("/welcome")
            call.respond(FreeMarkerContent("chatapp.ftlh", mapOf("messages" to Storage.messages, "username" to username)))
        }

        get("/welcome")
        {
            call.respondFile(File(this::class.java.getResource("/welcome.html").toURI()))
        }

        post("/login") {
            val formParams = call.receiveParameters()
            val username = formParams["username"] ?: return@post call.respondText("Missing username")

            call.response.cookies.append(Cookie("username", username))
            call.respondRedirect("/")
        }
    }
}
