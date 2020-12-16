package com.github.shixiaoyanger.miraiBot.utils

import cn.hutool.http.HtmlUtil
import org.jsoup.Jsoup

object HtmlUtil {
    fun phaseImage(bodyHtml: String): List<String> {
        val imageList = mutableListOf<String>()

        Jsoup.parseBodyFragment(bodyHtml).body().getElementsByTag("img").forEach { imgs ->
            imgs.attr("src")?.let {
                imageList.add(it)
            }
        }

        return imageList
    }

    fun cleanHtmlTag(content: String): String = HtmlUtil.cleanHtmlTag(content.replace("<br>", "\n"))


}
