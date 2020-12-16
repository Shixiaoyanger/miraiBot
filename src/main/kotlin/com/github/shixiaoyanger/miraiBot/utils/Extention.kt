package com.github.shixiaoyanger.miraiBot.utils

import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import java.awt.Graphics


fun Graphics.drawVerticalString(str: String, x: Int, y: Int) {
    var lastY = y
    for (element in str) {
        lastY += this.fontMetrics.charWidth(element) + this.fontMetrics.descent
        this.drawString(element.toString(), x, lastY)

    }
}

/**
 * 生成[MessageChain]， 当[MessageChain]内容为空时，添加默认内容[defaultMessage]
 *
 * [MessageChain]不为空时[defaultMessage]不会被添加
 */
fun MessageChainBuilder.build(defaultMessage: String): MessageChain {
    val messageChain = this.build()
    return if (messageChain.isEmpty()) {
        messageChain + defaultMessage
    } else {
        messageChain
    }
}

