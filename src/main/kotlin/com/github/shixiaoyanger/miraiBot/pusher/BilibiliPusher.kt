package com.github.shixiaoyanger.miraiBot.pusher

import net.mamoe.mirai.Bot
import java.util.concurrent.TimeUnit

/**
 *@description bilibili推送相关
 *
 **/
class BilibiliPusher : Pusher {
    override val name: String = "bilibiliPusher"
    override val period: Long = 10 * 60 //10分钟
    override val initialDelay: Long = 0
    override val timeUnit: TimeUnit = TimeUnit.SECONDS
    override var bot: Bot? = null

    override fun push() {
        TODO("Not yet implemented")
    }

}