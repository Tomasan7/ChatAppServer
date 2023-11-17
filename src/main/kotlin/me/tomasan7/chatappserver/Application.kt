package me.tomasan7.chatappserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.tomasan7.chatappserver.plugins.configureRouting
import me.tomasan7.chatappserver.plugins.configureSockets
import me.tomasan7.chatappserver.plugins.configureTemplating

fun main()
{
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val host = System.getenv("BIND_ADDRESS") ?: "0.0.0.0"

    embeddedServer(Netty, port = port, host = host, module = Application::module)
        .start(wait = true)
}

fun Application.module()
{
    configureTemplating()
    configureSockets()
    configureRouting()
}
