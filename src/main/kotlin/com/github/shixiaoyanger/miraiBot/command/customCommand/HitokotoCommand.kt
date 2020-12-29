package com.github.shixiaoyanger.miraiBot.command.customCommand

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.command.ChatCommand
import com.github.shixiaoyanger.miraiBot.model.hitokoto.Hitokoto
import com.github.shixiaoyanger.miraiBot.utils.asMessageChain
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain


/**
 * 一言
 */
class HitokotoCommand : ChatCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        return getHitokoto().asMessageChain()
    }

    override fun getPrefix(): List<String> {
        return listOf("一言")
    }

    override fun getName(): String {
        return "/一言 随机的一句话"
    }

    override fun getHelp(): String {
        return "/一言"
    }

    fun getHitokoto(): String {
        return try {
            val data = HttpUtil.get("https://v1.hitokoto.cn/")
            val hitokoto: Hitokoto = BotData.json.decodeFromString(data)
            hitokoto.content
        } catch (e: Exception) {
            serviceLogger.verbose("getHitokoto,error, ${e.message}")

            "我累了，才不想和你说呢！"
        }
    }
}