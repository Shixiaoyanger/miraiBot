package com.github.shixiaoyanger.miraiBot.model.bilibili

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.bot.BotData.json

object BilibiliApi {
    private const val baseUrl = "https://api.bilibili.com/x/web-interface/search/type"

    fun search(keyword: String, searchType: SearchType): SearchResult.Data {
        val params = mapOf(
                "search_type" to searchType.value,
                "keyword" to keyword
        )
        val data = HttpUtil.get(baseUrl, params)

        val result: SearchResult = json.decodeFromString(SearchResult.serializer(), data)
        return result.data
    }
}

enum class SearchType(val value: String) {
    /**
     * 视频
     */
    VIDEO("video"),

    /**
     * 番剧
     */
    BANGUMI("media_bangumi"),

    /**
     * 直播
     */
    LIVE("live"),

    /**
     * 专栏
     */
    ARTICLE("article"),

    /**
     * 用户
     */
    USER("bili_user")

}