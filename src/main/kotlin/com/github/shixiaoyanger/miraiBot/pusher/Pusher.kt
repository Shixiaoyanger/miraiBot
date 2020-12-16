package com.github.shixiaoyanger.miraiBot.pusher

import net.mamoe.mirai.Bot
import java.util.concurrent.TimeUnit

/**
 * 定时推送
 */
interface Pusher {
    val name: String
    val period: Long
    val initialDelay: Long
    val timeUnit: TimeUnit
    var bot: Bot?

    /**
     * 推送
     */
    fun push()
}