package me.tomasan7.chatappserver

import io.ktor.server.websocket.*

data class ChatAppSession(val username: String, val session: DefaultWebSocketServerSession)
