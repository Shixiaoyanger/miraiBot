package com.github.shixiaoyanger.miraiBot.command.customCommand

import com.github.shixiaoyanger.miraiBot.command.ChatCommand
import com.github.shixiaoyanger.miraiBot.command.CommandExecutor.isCommand
import com.github.shixiaoyanger.miraiBot.model.translate.Google
import com.github.shixiaoyanger.miraiBot.model.translate.Language
import com.github.shixiaoyanger.miraiBot.utils.asMessageChain
import com.github.shixiaoyanger.miraiBot.utils.isEnglish
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain

/**
 * 语言翻译
 */
class TranslateCommand : ChatCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        // 无内容 返回帮助
        args.getOrNull(1) ?: return getHelp().asMessageChain()

        // 获取要翻译的内容、源语言、目标语言
        val (text, from, to) = guessLanguage(args.subList(1, args.size))
        if (text.isBlank()) {
            return "请输入内容".asMessageChain()
        }
        // 默认谷歌翻译，备用有道翻译
        val result = Google.translate(text, from, to)
        return if (result.isNotBlank()) {
            result
        } else {
            "出错啦，再试一次吧"
        }.asMessageChain()
    }

    override fun getPrefix(): List<String> {
        return listOf("翻译", "translate", "trans")
    }

    override fun getName(): String {
        return "/翻译 你的翻译小助手"
    }

    override fun getHelp(): String {
        return """
            欢迎使用翻译功能
            
            🔹 /翻译 内容
            🔹 /翻译 /中英 内容
            
            使用样例：
            /翻译 /中英 你好
       
            当前支持的语言有：
            中文、英语、日语、俄语、德语、法语、西班牙语、意大利语
            
            提示：
            🔸 每个参数之间用一个空格隔开
            🔸 /中英 省略则自动判断语言
            🔸 /中英 可更换为 /英日 等各种语言组合
            🔸 可以使用的命令有：/${getPrefix().joinToString("  /")}
        """.trimIndent()
    }

    private fun guessLanguage(args: List<String>): Result {
        val t = mutableListOf("自", "自")
        val text = if (isCommand(args[0]) && args[0].length >= 3) {
            // 获取翻译目标 如 中英 ，则t[0] = "中", t[1] = "英"
            t[0] = args[0][1].toString()
            t[1] = args[0][2].toString()
            // 翻译英文等可能有空格，要将单词拼成一句话
            args.drop(1).joinToString(" ")
        } else {
            args.joinToString(" ").also {
                // 谷歌翻译 auto -> auto 英语翻译结果还是英语，要加点儿限制
                if (it.isEnglish()) {
                    t[0] = "英"
                    t[1] = "中"
                }
            }
        }
        return Result(
                // 要翻译的内容
                text,
                // 根据前缀获取对应语言的枚举
                enumValues<Language>().firstOrNull {
                    it.meaning.startsWith(t[0])
                } ?: Language.AUTO,
                // 同上
                enumValues<Language>().firstOrNull {
                    it.meaning.startsWith(t[1])
                } ?: Language.AUTO
        )
    }

    data class Result(val text: String, val from: Language, val to: Language)

}