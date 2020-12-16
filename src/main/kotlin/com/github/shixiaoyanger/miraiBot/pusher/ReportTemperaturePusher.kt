package com.github.shixiaoyanger.miraiBot.pusher

import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.getDifTime
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import java.util.concurrent.TimeUnit

/**
 *@description 体温填报打卡提醒
 **/
class ReportTemperaturePusher : Pusher {
    override val name: String = "ReportTemperaturePusher"
    override val period: Long = 24 * 60 * 60

    override val initialDelay: Long
        get() {
            val now = System.currentTimeMillis()
            val difTime = getDifTime(now, hourOfDay = 19)
            return if (difTime > 0) {
                difTime / 1000
            } else {
                period - difTime / 1000
            }
        }

    override val timeUnit: TimeUnit = TimeUnit.SECONDS

    override var bot: Bot? = null


    override fun push() {
        runBlocking {
            bot?.getGroup(0L)?.sendMessage("别忘了今天的打卡哦")
        }
    }
}