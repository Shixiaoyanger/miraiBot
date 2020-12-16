package com.github.shixiaoyanger.miraiBot.utils


import cn.hutool.core.io.file.FileWriter
import com.github.shixiaoyanger.miraiBot.bot.BotData
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.getDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

@Synchronized
fun File.writeAppend(content: String) {
    if (!this.exists()) {
        this.createNewFile()
    }
    FileWriter.create(this).write(content, true)

}

// it will overwrite file content
fun File.writeText(content: String) {
    if (!this.exists()) {
        this.createNewFile()
    }
    FileWriter.create(this).write(content, false)
}

inline fun <reified T> File.writeJson(content: T) {
    if (!this.exists()) {
        this.createNewFile()
    }
    FileWriter.create(this).write(json.encodeToString(content), false)

}

fun File.readContent(): String {
    if (!this.exists()) {
        this.createNewFile()
    }
    return this.readText()
}

inline fun <reified T> File.loadObject(): T {

    val data = this.readContent()
    return if (data.isNotBlank()) {
        json.decodeFromString(data)
    } else {
        json.decodeFromString("{}")
    }
}


object FileUtil {
    fun initLog() {
        try {
            val dateTime = getDateTime(System.currentTimeMillis(), TimeUtils.Format.ymd)
            val month = getDateTime(System.currentTimeMillis(), TimeUtils.Format.ym)
            val dir = getDictionary("logs/$month")
            BotData.log = File(dir, "service-log $dateTime.log").also {
                println(it.absoluteFile)
            }

            BotData.log.createNewFile()

            BotData.defaultLog = File(dir, "default-log $dateTime.log")
            BotData.defaultLog.createNewFile()
        } catch (e: IOException) {
            println("log文件创建失败")
            throw e
        }
    }

    fun getDictionary(path: String): File {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

}