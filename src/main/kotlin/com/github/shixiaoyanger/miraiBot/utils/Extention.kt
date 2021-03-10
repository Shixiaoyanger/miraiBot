package com.github.shixiaoyanger.miraiBot.utils

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


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

fun BufferedImage.uploadAsImage(contact: Contact): Image {
    ByteArrayOutputStream().use { os ->
        ImageIO.write(this, "png", os)
        return runBlocking { ByteArrayInputStream(os.toByteArray()).use { it.uploadAsImage(contact) } }
    }
}

fun String.asMessageChain(): MessageChain {
    return PlainText(this).toMessageChain()
}

// 判断是否是英文
fun String.isEnglish(): Boolean {
    this.forEach {
        if (it.toInt() > 128) {
            return false
        }
    }
    return true
}

