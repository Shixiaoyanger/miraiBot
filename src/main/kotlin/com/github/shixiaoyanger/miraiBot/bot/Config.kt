package com.github.shixiaoyanger.miraiBot.bot

import kotlinx.serialization.Serializable
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.yamlkt.Comment


@Serializable
data class Config(
        @Comment("QQ账号相关配置")
        val qqAccount: QQAccount = QQAccount(0L, "", 0L),
        @Comment("心跳周期. 过长会导致被服务器断开连接，默认60s")
        val heartbeatPeriod: Int = 60,
        @Comment("识别为命令的符号，默认 \".\"  \"。\" \"!\"  \"/\"")
        val commandPrefix: MutableList<String> = mutableListOf(".", "。", "!", "/"),
        @Comment("定时任务线程池 默认 poolSize = 4")
        val threadPoolSize: Int = 4,
        @Comment("使用的协议类型 默认 Android 手机 可选的有：\nANDROID_PHONE ：Android 手机.\nANDROID_PAD ：Android 平板.\nANDROID_WATCH ：Android 手表.")
        val protocol: BotConfiguration.MiraiProtocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE,
        @Comment("搜图相关")
        val imageSearch: ImageSearch = ImageSearch("", false)
) {
    @Serializable
    data class QQAccount(
            @Comment("QQ账号")
            var qq: Long = 0L,
            @Comment("QQ账号密码")
            var password: String = "",
            @Comment("管理员账号，管理机器人和接收机器人状态消息")
            val adminQQ: Long = 0L
    )

    @Serializable
    data class ImageSearch(
            @Comment("sauceNao API Key")
            val sauceNao: String = "",
            @Comment("true 开启，false 关闭。关闭后发涩图功能只能发全年龄图片。")
            val adultMode: Boolean = false
    )
}