package com.mkitsimple.counterboredom2.data.models

class ImageMessage(
    override val id: String,
    val imagePath: String,
    override val fromId: String,
    override val toId: String,
    override val timestamp: Long,
    override val seen: Boolean,
    override val type: String = MessageType.IMAGE

) : Message {
    constructor() : this("", "", "", "", -1,false,"")
}