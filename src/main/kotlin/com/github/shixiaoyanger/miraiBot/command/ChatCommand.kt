package com.github.shixiaoyanger.miraiBot.command

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain

interface ChatCommand {
    suspend fun execute(event: MessageEvent, args: List<String>): MessageChain

    /**
     * 获取命令前缀
     */
    fun getPrefix(): List<String>

    /**
     * 获取命令名（介绍）
     */
    fun getName(): String

    /**
     * 获取该命令的帮助
     */
    fun getHelp(): String

}