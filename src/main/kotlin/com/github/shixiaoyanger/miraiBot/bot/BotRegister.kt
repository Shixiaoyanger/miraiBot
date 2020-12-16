package com.github.shixiaoyanger.miraiBot.bot

import com.github.shixiaoyanger.miraiBot.bot.BotData.config
import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLog
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.command.CommandExecutor
import com.github.shixiaoyanger.miraiBot.command.CommandExecutor.executeCommand
import com.github.shixiaoyanger.miraiBot.command.CustomCommand.ImageSearchCommand
import com.github.shixiaoyanger.miraiBot.command.CustomCommand.RsshubCommand
import com.github.shixiaoyanger.miraiBot.command.CustomCommand.Splatoon2Command
import com.github.shixiaoyanger.miraiBot.pusher.ReportTemperaturePusher
import com.github.shixiaoyanger.miraiBot.pusher.RsshubPusher
import com.github.shixiaoyanger.miraiBot.utils.TaskUtil.runTask
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.getDateTime
import com.github.shixiaoyanger.miraiBot.utils.writeAppend
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.isBotMuted
import net.mamoe.mirai.event.*
import net.mamoe.mirai.getFriendOrNull
import net.mamoe.mirai.join
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.utils.PlatformLogger
import net.mamoe.mirai.utils.secondsToMillis
import java.util.concurrent.TimeUnit


suspend fun startQQBot() {
    val bot = Bot(
            config.qqAccount.qq,
            config.qqAccount.password
    ) {
        // 覆盖默认的配置
        protocol = config.protocol
        heartbeatPeriodMillis = config.heartbeatPeriod.secondsToMillis
        botLoggerSupplier = { it ->
            PlatformLogger("Bot${it.id}", {
                defaultLog.writeAppend(it + "\n")
                println(it)
            })
        }
        networkLoggerSupplier = { it ->
            PlatformLogger("Net${it.id}", {
                defaultLog.writeAppend(it + "\n")
                println(it)
            })
        }

        fileBasedDeviceInfo("device.json") // 使用 "device.json" 保存设备信息
        // networkLoggerSupplier = { SilentLogger } // 禁用网络层输出
    }.alsoLogin()

    try {
        // 注册命令
        registerCommand()
        // 注册推送服务
        registerPushers(bot)
        // 加载数据
        BotData.loadData()
        // 停止服务时保存数据
        Runtime.getRuntime().addShutdownHook(Thread {
            BotData.saveData()
            runBlocking { bot.getFriendOrNull(config.qqAccount.adminQQ)?.sendMessage("数据保存成功") }
        })
        // 定时保存数据
        runTask(1, 1, TimeUnit.HOURS) { BotData.saveData() }

        // 初始化结束，监听消息
        bot.messageDSL()

        serviceLogger.info("start QQ bot successfully!")
    } catch (e: Exception) {

        bot.getFriendOrNull(config.qqAccount.adminQQ)?.sendMessage("机器人启动时发生了一些错误，${e.message}")
    }
    val time = getDateTime(System.currentTimeMillis(), TimeUtils.Format.hms)
    bot.getFriendOrNull(config.qqAccount.adminQQ)?.sendMessage("机器人在$time 启动成功")

    bot.join()//等到直到断开连接
}


/**
 * 使用 dsl 监听消息事件
 *
 * @see subscribeFriendMessages
 * @see subscribeMessages
 * @see subscribeGroupMessages
 * @see subscribeTempMessages
 *
 * @see MessageSubscribersBuilder
 */
fun Bot.messageDSL() {
    // 监听这个 bot 的来自所有群和好友的消息
    this.subscribeMessages {
        always {
            if (this is GroupMessageEvent && group.isBotMuted) return@always

            val result = executeCommand(this)
            println(result)
            if (!result.isEmpty()) {
                this.subject.sendMessage(result)
            }
        }
    }
}


fun registerCommand() {
    CommandExecutor.addCommand(
            listOf(
                    Splatoon2Command(),
                    RsshubCommand(),
                    ImageSearchCommand()
            )

    )
}

fun registerPushers(bot: Bot) {
    listOf(
            ReportTemperaturePusher(),
            RsshubPusher(),
    ).forEach {
        it.bot = bot
        runTask(it)
    }
}