package me.tomasan7.chatappserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import me.tomasan7.chatappserver.plugins.configureRouting
import me.tomasan7.chatappserver.plugins.configureSockets
import me.tomasan7.chatappserver.plugins.configureTemplating
import org.slf4j.event.*

fun main()
{
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module()
{
    configureTemplating()
    configureSockets()
    configureRouting()
}

fun ApplicationCall.getUsername() = request.cookies["username"]
