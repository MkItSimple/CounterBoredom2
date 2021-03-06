package com.mkitsimple.counterboredom2.data.models

class ChatMessage(
    override val id: String,
    val text: String,
    override val fromId: String,
    override val toId: String,
    override val timestamp: Long,
    override val type: String = MessageType.TEXT
) :  Message {
    constructor() : this("", "", "", "", -1, "")
}