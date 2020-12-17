package com.github.shixiaoyanger.miraiBot.model.hitokoto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hitokoto(
        @SerialName("hitokoto")
        val content: String = "我累了，才不想和你说呢！",
        @SerialName("from")
        val source: String? = "",
        @SerialName("from_who")
        val author: String? = ""
)
