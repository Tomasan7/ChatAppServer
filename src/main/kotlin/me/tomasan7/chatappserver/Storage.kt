package me.tomasan7.chatappserver

import java.util.*

object Storage
{
    val connections = Collections.synchronizedMap(mutableMapOf<String, ChatAppSession>())
    val messages = Collections.synchronizedList(mutableListOf<Message>())
}
