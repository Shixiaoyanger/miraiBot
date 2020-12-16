package com.github.shixiaoyanger.miraiBot.model.bilibili

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * bilibili搜索番剧，只包括部分字段
 */
@Serializable
data class SearchResult(
        val data: Data
) {
    @Serializable
    class Data(
            val result: List<Result>
    ) {
        @Serializable
        class Result(
                @SerialName("media_id")
                val mediaID: Int,
                val title: String,
                val cover: String,
                @SerialName("goto_url")
                val url: String
        )
    }
}
