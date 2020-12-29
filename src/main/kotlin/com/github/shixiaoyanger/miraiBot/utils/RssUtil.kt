package com.github.shixiaoyanger.miraiBot.utils


import cn.hutool.http.HttpRequest
import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLogger
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.model.rsshub.RsshubEntry
import com.github.shixiaoyanger.miraiBot.model.rsshub.RsshubFeed
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import java.io.InputStream

object RssUtil {
    fun updateRsshub(newFeed: RsshubFeed, oldFeed: RsshubFeed): List<RsshubEntry> {

        if (newFeed.lastBuildDate <= oldFeed.lastBuildDate) return emptyList()

        var updated = false
        val newEntries = mutableListOf<RsshubEntry>()
        val newItemLinks = mutableListOf<String>()

        newFeed.items.forEach {
            if (it.link !in oldFeed.itemLinks) {
                newEntries.add(it)
                updated = true
            }
            newItemLinks.add(it.link)
        }

        if (updated) {
            serviceLogger.debug(oldFeed.itemLinks.toString())
            serviceLogger.debug(newItemLinks.toString())

            oldFeed.title = newFeed.title
            oldFeed.link = newFeed.link
            oldFeed.description = newFeed.description
            oldFeed.itemLinks = newItemLinks
            oldFeed.lastBuildDate = newFeed.lastBuildDate

            BotData.rsshubFeeds[oldFeed.subscribeUrl] = oldFeed
        }

        return newEntries
    }

    /**
     * 将rss XML解析成 [RsshubFeed]
     */
    fun getRsshubSource(url: String): RsshubFeed {
        //TODO 重定向
        val result = HttpRequest.get(url).timeout(10_000).let {
            println(it.headers())
            it.execute()
        }


        val feed: SyndFeed = SyndFeedInput().build(XmlReader(result.bodyStream()))

        val rsshubEntries = mutableListOf<RsshubEntry>()
        val entryLinks = mutableListOf<String>()

        feed.entries.forEach {

            val imageUrl = HtmlUtil.phaseImage(it.description.value).getOrNull(0)
            val description = HtmlUtil.cleanHtmlTag(it.description.value)
            rsshubEntries.add(
                    RsshubEntry(it.title, description, imageUrl, it.publishedDate.time, it.link)
            )
            entryLinks.add(it.link)
        }

        return RsshubFeed(
                subscribeUrl = url,
                title = feed.title,
                link = feed.link,
                description = feed.description,
                lastBuildDate = feed.publishedDate.time,
                itemLinks = entryLinks,
                items = rsshubEntries,
        )

    }

    fun getImage(imageUrl: String?): InputStream? {
        if (imageUrl == null) return null
        return try {
            defaultLogger.verbose("RssUtil.getImage:开始下载图片")
            val response = HttpRequest.get(imageUrl).execute()
            return response.bodyStream()
        } catch (e: Exception) {
            serviceLogger.verbose("RssUtil.getImage:${e.message}, $imageUrl", e)
            null
        }
    }
}