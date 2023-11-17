package me.tomasan7.chatappserver

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
)
