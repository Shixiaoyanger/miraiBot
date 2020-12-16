package com.github.shixiaoyanger.miraiBot.pusher


import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.model.rsshub.*
import com.github.shixiaoyanger.miraiBot.utils.RssUtil
import com.github.shixiaoyanger.miraiBot.utils.RssUtil.getRsshubSource
import com.github.shixiaoyanger.miraiBot.utils.RssUtil.updateRsshub
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.upload
import java.util.concurrent.TimeUnit

class RsshubPusher : Pusher {
    override val name: String = "RsshubPusher"
    override val period: Long = 10

    override val initialDelay: Long = 1

    override val timeUnit: TimeUnit = TimeUnit.MINUTES

    override var bot: Bot? = null


    override fun push() {

        BotData.rsshubFeeds.values.forEach { oldFeed ->
            lateinit var feed: RsshubFeed
            try {
                feed = getRsshubSource(oldFeed.subscribeUrl)
            } catch (e: Exception) {
                serviceLogger.verbose("获取rsshub feed失败 ${e.message} ${oldFeed.subscribeUrl}")
                return@forEach
            }
            val entries = updateRsshub(feed, oldFeed)
            if (entries.isEmpty()) return@forEach


            BotData.groups.values.parallelStream()
                    .filter { it.rsshubSubscribeLinks.contains(oldFeed.subscribeUrl) }
                    .forEach { runBlocking { sendMessage(bot?.getGroup(it.id), entries, feed.title) } }

            BotData.users.values.parallelStream()
                    .filter { it.rsshubSubscribeLinks.contains(oldFeed.subscribeUrl) }
                    .forEach { runBlocking { sendMessage(bot?.getGroup(it.id), entries, feed.title) } }


        }
    }


    private suspend fun sendMessage(contact: Contact?, entries: List<RsshubEntry>, title: String) {
        if (contact == null) return
        val message = buildMessageChain {
            add("你订阅的${title}更新啦！\n")
            entries.forEach { entry ->
                add("\n标题：${entry.title}\n")
                RssUtil.getImage(entry.imageUrl)?.let { add(it.upload(contact)) }
                add("${entry.description}\n")
                add("内容链接：${entry.link}\n")
            }
        }
        try {
            contact.sendMessage(message)
        } catch (e: Exception) {
            serviceLogger.warning("发送rss订阅消息失败 ${e.message}")
        }
        delay(500L)
    }


}