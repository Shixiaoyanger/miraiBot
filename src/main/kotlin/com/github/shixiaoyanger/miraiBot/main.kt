package com.github.shixiaoyanger.miraiBot

import com.github.shixiaoyanger.miraiBot.bot.startQQBot
import com.github.shixiaoyanger.miraiBot.utils.FileUtil.initLog


suspend fun main() {
    // 初始化log文件
    initLog()
    // 启动QQ
    startQQBot()
}