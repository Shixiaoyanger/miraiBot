package com.github.shixiaoyanger.miraiBot.command.customCommand

import cn.hutool.core.io.IORuntimeException
import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.bot.Group.Companion.getGroup
import com.github.shixiaoyanger.miraiBot.bot.User.Companion.getUser
import com.github.shixiaoyanger.miraiBot.command.ChatCommand
import com.github.shixiaoyanger.miraiBot.utils.RssUtil.getImage
import com.github.shixiaoyanger.miraiBot.utils.RssUtil.getRsshubSource
import com.github.shixiaoyanger.miraiBot.utils.build
import com.rometools.rome.io.ParsingFeedException
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.events.FriendEvent
import net.mamoe.mirai.event.events.GroupEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

class RsshubCommand : ChatCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        //TODO help
        if (args.size < 2) {
            return buildMessageChain { add(getHelp()) }
        }
        return when (args[1]) {
            "订阅", "sub" -> subscribe(args.getOrNull(2), event)
            "退订", "unsub" -> unSubscribe(args.getOrNull(2), event)
            "列表", "list" -> listSubscribe(event)

            else -> buildMessageChain { add(getHelp()) }
        }

    }

    override fun getPrefix(): List<String> {
        return listOf("rss", "rsshub")
    }

    override fun getName(): String {
        return "/rss （rss订阅）"
    }

    override fun getHelp(): String {
        return """
            欢迎使用rss订阅功能
            
            🔹 /rss 订阅/sub rss链接
            🔹 /rss 退订/unsub rss链接
            🔹 /rss 列表/list
            🔹 /rss 帮助/help
            🔹 /rss 源
            （了解如何获取rss源）
            
            使用样例：
            /rss 订阅 https://rakuen.thec.me/PixivRss/daily-10
            
            提示：
            🔸 每个参数之间用一个空格隔开
        """.trimIndent()
    }

    private fun subscribe(url: String?, event: MessageEvent): MessageChain {
        val message = MessageChainBuilder()

        url ?: return message.build("缺少参数：订阅链接")

        try {
            val feed = getRsshubSource(url)
            if (!BotData.rsshubFeeds.containsKey(url)) {
                BotData.rsshubFeeds[url] = feed
            }
            when (event) {
                //TODO 确认事件个数
                is GroupEvent -> getGroup(event.subject.id).rsshubSubscribeLinks.add(url)
                is FriendEvent, is GroupTempMessageEvent -> getUser(event.subject.id).rsshubSubscribeLinks.add(url)
            }

            message.add("订阅成功!\n")
            message.add("订阅主题：${feed.title}\n")
            feed.items.getOrNull(0)?.let { entry ->
                message.add("标题：${entry.title}\n")
                runBlocking { getImage(entry.imageUrl)?.use { message.add(it.uploadAsImage(event.subject)) } }
                message.add("${entry.description}\n")
                message.add("内容链接：${entry.link}")
            }

        } catch (e: Exception) {

            when (e) {
                // feed解析失败
                is ParsingFeedException -> {
                    message.add("解析失败，请提供有效的rss源")
                }
                // 请求超时 |
                is IORuntimeException -> {
                    message.add("rss链接访问超时，你可以再试一次")
                }
                else -> {
                    serviceLogger.error("RsshubCommand 未知错误 ${e.message} ${e.stackTrace}", e)
                    message.add("呜呜，发生了一些错误，请稍后重试")
                }

            }
        }
        return message.build()
    }

    private fun unSubscribe(url: String?, event: MessageEvent): MessageChain {
        val message = MessageChainBuilder()

        url ?: return message.build("缺少参数：订阅链接")

        val deleted = when (event) {
            //TODO 确认事件个数
            is GroupEvent -> getGroup(event.subject.id).rsshubSubscribeLinks.remove(url)
            is FriendEvent -> getUser(event.subject.id).rsshubSubscribeLinks.remove(url)
            else -> false
        }

        return if (deleted) {
            message.build("退订成功！")
        } else {
            message.build("你没有订阅，请重试！")
        }
    }


    private fun listSubscribe(event: MessageEvent): MessageChain {
        val message = MessageChainBuilder()
        val subscribeLinks =
                when (event) {
                    //TODO 确认事件个数
                    is GroupEvent -> getGroup(event.subject.id).rsshubSubscribeLinks
                    is FriendEvent -> getUser(event.subject.id).rsshubSubscribeLinks

                    else -> emptySet()
                }
        subscribeLinks.forEach { link ->
            val feed = BotData.rsshubFeeds[link]
            feed?.let {
                message.add("${it.title}  ${it.subscribeUrl}\n")
            }
        }

        return message.build("你还没有任何订阅哦！")
    }

}