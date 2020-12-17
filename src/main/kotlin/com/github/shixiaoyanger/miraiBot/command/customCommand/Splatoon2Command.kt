package com.github.shixiaoyanger.miraiBot.command.customCommand

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.bot.BotData.json
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.command.ChatCommand
import com.github.shixiaoyanger.miraiBot.model.splatoon.CoopSchedules
import com.github.shixiaoyanger.miraiBot.model.splatoon.Schedule
import com.github.shixiaoyanger.miraiBot.model.splatoon.consts.*
import com.github.shixiaoyanger.miraiBot.model.splatoon.impl.DrawImageImpl
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.upload
import java.awt.image.BufferedImage

class Splatoon2Command : ChatCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {

        if (args.size < 2) {
            return buildMessageChain { add(getHelp()) }
        }

        return buildMessageChain {
            if (event is GroupMessageEvent) add(At(event.sender))

            when (args[1]) {
                "下", "下图", "图" -> getSchedule(0)?.let { add(it.upload(event.subject)) }

                "下下", "下下图", "图图" -> {
                    getSchedule(0)?.let { add(it.upload(event.subject)) }
                    getSchedule(1)?.let { add(it.upload(event.subject)) }

                }
                "工", "打工" -> getCoopSchedule()?.let { add(it.upload(event.subject)) }

                else -> getHelp()
            }
        }

    }

    override fun getPrefix(): List<String> {
        return listOf("乌贼", "喷射战士", "喷喷", "splatoon", "spl")

    }

    override fun getName(): String {
        return "/乌贼 （喷射战士单排、组排图，打工图）"
    }

    override fun getHelp(): String {
        return """
            欢迎使用喷射战士查询功能
            可以查询的有：
            单排、组排、涂地地图
            打工武器和地图
            
            🔹 /乌贼 下/图/下图
            （获取当前地图）
            🔹 /乌贼 下下/图图/下下图
            （获取当前和下一阶段地图）
            🔹 /乌贼 工/打工
            （获取打工地图）
      
            使用样例:
            /乌贼 下图
            
            提示：
            🔸 乌贼也可以用“喷射战士”、“喷喷”、“splatoon”、“spl”替换
            🔸 每个参数之间用一个空格隔开
        """.trimIndent()
    }

    private fun getSchedule(idx: Int = 0): BufferedImage? {
        val cache = BotData.splatoonCache
        var refresh = false

        val data = if (cache != null && cache.expire > System.currentTimeMillis()) {
            cache.data
        } else {
            refresh = true
            try {
                HttpUtil.get(scheduleDataURL)
            } catch (e: Exception) {
                serviceLogger.warning("获取喷射战士日程信息失败${e.message}")
                return null
            }
        }

        val schedule: Schedule = json.decodeFromString(data)

        //设置缓存
        if (refresh) BotData.splatoonCache = BotData.SplatoonData(data, schedule.gachiList[0].endTime * 1000)
        serviceLogger.verbose0("喷射战士schedule日程安排缓存更新成功")


        val drawer = DrawImageImpl(ScheduleMapWidth, ScheduleMapHeight, scheduleColor)
        return try {
            drawer.drawSchedule(schedule, idx)
        } catch (e: Exception) {
            serviceLogger.error("喷射战士画涂地、真格、组排日程安排失败", e)
            null
        }
    }

    private fun getCoopSchedule(): BufferedImage? {
        val cache = BotData.splatoonCache
        var refresh = false

        val data = if (cache != null && cache.expire > System.currentTimeMillis()) {
            cache.data
        } else {
            refresh = true
            try {
                HttpUtil.get(coopScheduleDataURL)
            } catch (e: Exception) {
                serviceLogger.warning("获取喷射战士打工信息失败 ${e.message}")
                return null
            }
        }
        val coopSchedules: CoopSchedules = json.decodeFromString(data)

        //设置缓存
        if (refresh) BotData.splatoonCache = BotData.SplatoonData(data, coopSchedules.details[0].endTime * 1000)
        serviceLogger.verbose0("喷射战士coopSchedule打工安排缓存更新成功")

        val drawer = DrawImageImpl(CoopScheduleWidth, CoopScheduleHeight, gachiColor)
        return try {
            drawer.drawCoopSchedule(coopSchedules)
        } catch (e: Exception) {
            serviceLogger.error("喷射战士画鲑鱼打工图失败", e)
            null
        }
    }
}