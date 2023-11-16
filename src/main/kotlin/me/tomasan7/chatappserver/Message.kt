package me.tomasan7.chatappserver

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val author: String,
    val content: String,
    val datetime: Instant = Clock.System.now(),
)
