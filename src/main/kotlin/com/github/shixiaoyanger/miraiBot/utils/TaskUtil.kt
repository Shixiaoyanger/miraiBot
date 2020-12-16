package com.github.shixiaoyanger.miraiBot.utils

import com.github.shixiaoyanger.miraiBot.bot.BotData.scheduledPool
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.pusher.Pusher
import java.util.concurrent.TimeUnit

/**
 * 定时任务相关工具类
 */
object TaskUtil {
    fun runTask(initialDelay: Long, period: Long, unit: TimeUnit = TimeUnit.SECONDS, t: () -> Unit) {
        scheduledPool.scheduleAtFixedRate(t, initialDelay, period, unit)
    }

    fun runTask(pusher: Pusher) {
        runTask(pusher.initialDelay, pusher.period, pusher.timeUnit) {
            serviceLogger.verbose0("${pusher.name}开始执行")
            val start = System.currentTimeMillis()
            try {
                pusher.push()
            } catch (e: Exception) {
                serviceLogger.error("runTask error! ${pusher.name}", e)
            }
            val end = System.currentTimeMillis()
            serviceLogger.verbose0("${pusher.name}执行结束，耗时${(end - start) / 1000}s")
        }
    }
}