package com.github.shixiaoyanger.miraiBot.model.imageSearch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// useless
@Serializable
data class SauceNao(
        val header: Header,
        val results: List<Result>
) {
    @Serializable
    data class Header(
            @SerialName("short_limit")
            val shortLimit: String,
            @SerialName("long_limit")
            val longLimit: String,
    )

    @Serializable
    data class Result(
            val header: Header,
            val data: Data
    ) {
        @Serializable
        data class Data(
                @SerialName("ext_urls")
                val extUrls: List<String>,
                val title: String
        )

        @Serializable
        data class Header(
                val similarity: String,
        )
    }
}