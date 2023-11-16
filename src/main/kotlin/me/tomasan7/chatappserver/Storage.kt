package me.tomasan7.chatappserver

import me.tomasan7.chatappserver.plugins.Connection
import java.util.*

object Storage
{
    val connections = Collections.synchronizedMap(mutableMapOf<String, Connection>())
    val messages: MutableList<Message> = mutableListOf()
}
