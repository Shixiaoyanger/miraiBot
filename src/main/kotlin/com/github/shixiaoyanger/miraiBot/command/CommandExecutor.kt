package com.github.shixiaoyanger.miraiBot.command

import com.github.shixiaoyanger.miraiBot.bot.BotData.config
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.util.*

/**
 * 处理聊天命令
 */
object CommandExecutor {
    var commands: MutableList<ChatCommand> = mutableListOf()
    val helpCommand = listOf("help", "帮助")

    /**
     * 添加命令
     */
    fun addCommand(cmds: List<ChatCommand>) {
        cmds.forEach {
            if (it !in commands) {
                commands.add(it)
            }
        }
    }

    /**
     * 执行命令
     */
    suspend fun executeCommand(event: MessageEvent): MessageChain {
        val msg = event.message[PlainText].toString()

        if (isCommand(msg)) {
            //args 命令名+操作名
            val args = msg.substring(1).split(" ")
            val command: Optional<ChatCommand> = getCommand(args[0])

            if (command.isPresent) {

                return command.get().execute(event, args)
            }
        } else if (msg in helpCommand) {
            return buildMessageChain {
                add("机器人目前开放的功能如下:\n\n")
                commands.forEach {
                    add("\uD83D\uDD39${it.getName()}\n")
                }
                add("\n获得进一步帮助，请输入命令如：/rss")
            }
        }
        return buildMessageChain { }
    }

    /**
     * 找到命令
     */
    fun getCommand(commandName: String): Optional<ChatCommand> {
        return commands.parallelStream().filter {
            it.getPrefix().contains(commandName)
        }.findFirst()
    }

    /**
     * 前缀匹配判断是否是命令
     */
    fun isCommand(msg: String): Boolean {
        config.commandPrefix.forEach {
            if (msg.startsWith(it)) return true
        }
        return false
    }

}