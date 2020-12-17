package com.github.shixiaoyanger.miraiBot.command.customCommand

import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLogger
import com.github.shixiaoyanger.miraiBot.command.ChatCommand
import com.github.shixiaoyanger.miraiBot.utils.ImageSearchUtil.Rating
import com.github.shixiaoyanger.miraiBot.utils.ImageSearchUtil.getKonachanImg
import com.github.shixiaoyanger.miraiBot.utils.ImageSearchUtil.sauceNaoSearch
import com.github.shixiaoyanger.miraiBot.utils.RssUtil.getImage
import com.github.shixiaoyanger.miraiBot.utils.build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.upload
import kotlin.system.measureTimeMillis

class ImageSearchCommand : ChatCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        return when (args[0]) {
            "搜图" -> searchImage(event)
            "setu", "涩图" -> getSetu(event, args)
            else -> buildMessageChain { add(getHelp()) }
        }
    }

    override fun getPrefix(): List<String> {
        return listOf("搜图", "setu", "涩图")
    }

    override fun getName(): String {
        return "/搜图  查找图片来源"
    }

    override fun getHelp(): String {
        return """
            欢迎使用搜图功能：
            
            使用样例：
            /搜图
            [图片]
            
            手机端查询方法：
            聊天框输入 /搜图 ，点击相册选择图片，点击发送         
        """.trimIndent()
    }

    private suspend fun searchImage(event: MessageEvent): MessageChain {
        val message = MessageChainBuilder()

        val imageUrl = event.message[Image]?.queryUrl() ?: return message.build(getHelp())

        val result = sauceNaoSearch(imageUrl)

        if (result.similarity > 50) {
            //TODO 发送原图
            val image = getImage(result.microUrl)
            result.title?.let { message.add("标题：$it\n") }
            withContext(Dispatchers.IO) { image?.let { message.add(it.upload(event.subject)) } }
            message.add("相似度：${result.similarity}\n")
            message.add("图片来源：${result.originUrl}")
        }

        return message.build("呜呜，没有找到符合的图片~")
    }

    suspend fun getSetu(event: MessageEvent, args: List<String>): MessageChain {

        val rating = when (args.getOrNull(1)) {
            "s", "safe" -> Rating.SAFE
            "q", "questionable" -> Rating.QUESTIONABLE
            "e", "explicit" -> Rating.EXPLICIT
            else -> Rating.SAFE
        }
        val cost = measureTimeMillis {
            val image = getKonachanImg(rating = rating)

            if (image != null) {
                event.reply("上传中~")
                // 发送时间取决于图片大小和带宽
                event.sendImage(image)
            } else {
                event.reply("机器人”哼“了一声，表示不想理你")
            }
        }
        defaultLogger.info("setu cost ${cost / 1000}s")

        return EmptyMessageChain
    }

}