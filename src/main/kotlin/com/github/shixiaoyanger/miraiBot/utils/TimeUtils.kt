package com.github.shixiaoyanger.miraiBot.utils

import java.text.SimpleDateFormat
import java.util.*


object TimeUtils {
    /**
     * 各种时间格式
     */
    enum class Format(val pattern: String) {
        full("yyyy-MM-dd HH:mm:ss"),
        ymd("yyyy-MM-dd"),
        ym("yyyy-MM"),
        mdhm("MM-dd HH:mm"),
        hm("HH:mm"),
        hms("HH:mm:ss")
    }

    /**
     * 时间戳转对应格式的时间字符串
     */
    fun getDateTime(timeStamp: Long, format: Format): String? {
        val dateFormat = SimpleDateFormat(format.pattern)
        return dateFormat.format(Date(timeStamp))
    }

    /**
     * 将时间戳差值转换为类似 10d5h30m 的格式
     *
     * @param difTime 时间戳差值 需要大于0
     */
    fun difTimeToStr(difTime: Long): String {
        if (difTime < 0) {
            throw Exception("difTime should bigger than 0 ")
        }
        val difMinutes = difTime / (60 * 1000) % 60
        val difHours = difTime / (60 * 60 * 1000) % 24
        val difDays = difTime / (24 * 60 * 60 * 1000)

        return StringBuilder().apply {
            if (difDays > 0) append("${difDays}d")
            if (difHours > 0) append("${difHours}h")
            if (difMinutes > 0) append("${difMinutes}m")
        }.toString()
    }

    fun getDifTime(start: Long, hourOfDay: Int = 0, minute: Int = 0, second: Int = 0): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }
        return calendar.time.time - start

    }

}