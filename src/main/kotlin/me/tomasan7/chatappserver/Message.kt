package me.tomasan7.chatappserver

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
)
