package com.github.shixiaoyanger.miraiBot

import com.github.shixiaoyanger.miraiBot.bot.BotData.config
import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLogger
import com.github.shixiaoyanger.miraiBot.bot.Config
import com.github.shixiaoyanger.miraiBot.bot.startQQBot
import com.github.shixiaoyanger.miraiBot.utils.FileUtil.initLog


suspend fun main() {
    // 初始化log文件
    initLog()
    // 启动QQ
    val (qq, password, adminQQ) = getQQAccount()
    defaultLogger.info("账号配置成功，正在登录QQ...")
    startQQBot(qq, password, adminQQ)

}

fun getQQAccount(): Config.QQAccount {
    if (config.qqAccount.qq != 0L && config.qqAccount.password.isNotBlank()) {
        return config.qqAccount
    }

    while (true) {
        try {
            defaultLogger.info("请输入你的QQ号：")
            print(">")
            val qqID = readLine()?.toLongOrNull() ?: 0L

            if (qqID == 0L) {
                defaultLogger.warning("请输入正确的QQ号!")
                continue
            }

            defaultLogger.info("输入你的QQ密码：")
            print(">")
            val password = readLine() ?: ""
            if (password.isBlank()) {
                defaultLogger.warning("密码不能为空，请重新输入!")
                continue
            }
            defaultLogger.info("输入你的管理员QQ号，用于管理机器人和接收机器人状态消息（需要是机器人的好友），如没有请直接回车（Enter）跳过：")
            print(">")
            val adminQQ = readLine()?.toLongOrNull() ?: 0L

            var confirm: String
            do {
                defaultLogger.info("你要登录的QQ号是：$qqID，你的管理员QQ是：$adminQQ\n确认登录请输入”yes“，输入有误请输入”no“")
                print(">")
                confirm = readLine().toString()
                println(confirm)
            } while (confirm !in setOf("yes", "no"))

            if (confirm == "no") continue

            config.qqAccount.qq = qqID
            config.qqAccount.password = password
            config.qqAccount.adminQQ = adminQQ

            return config.qqAccount
        } catch (e: Exception) {
            continue
        }
    }
}