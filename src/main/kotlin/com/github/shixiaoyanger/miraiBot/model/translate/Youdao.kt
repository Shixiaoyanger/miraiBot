package com.github.shixiaoyanger.miraiBot.model.translate

import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLogger
import com.github.shixiaoyanger.miraiBot.utils.json
import com.hiczp.bilibili.api.md5
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.random.Random

// 有道翻译
// 参考实现 https://github.com/skywind3000/translator/blob/master/translator.py
object Youdao {
    private const val host = "https://fanyi.youdao.com"
    private const val param = "/translate_o?smartresult=dict&smartresult=rule"

    private const val D = "97_3(jkMYg@T[KZQmqjTK"

    fun translate(text: String, from: Language = Language.AUTO, to: Language = Language.AUTO): String {

        val salt = System.currentTimeMillis() + Random.nextInt(0, 10)
        val sign = sign(text, salt)
        val header = mapOf(
                "Cookie " to "OUTFOX_SEARCH_USER_ID=-2022895048@10.168.8.76; ",
                "Referer " to "http to//fanyi.youdao.com/ ",
                "User-Agent " to "Mozilla/5.0 (Windows NT 6.2; rv to51.0) Gecko/20100101 Firefox/51.0 ",
        )
        val data = mapOf(
                "i" to text,
                "from" to "$from".toLowerCase(),
                "to" to "$to".toLowerCase(),
                "smartresult" to "dict",
                "client" to "fanyideskweb",
                "salt" to salt,
                "sign" to sign,
                "doctype" to "json",
                "version" to "2.1",
                "keyfrom" to "fanyi.web",
                "action" to "FY_BY_CL1CKBUTTON",
                "typoResult" to "true"
        )

        val request = HttpUtil.createRequest(Method.POST, host + param).addHeaders(header)

        return try {
            val result = request.form(data).execute().body()
            // 解析结果，失败直接throw
            json.parseToJsonElement(result).jsonObject["translateResult"]!!.jsonArray[0].jsonArray[0].jsonObject["tgt"]!!.jsonPrimitive.content

        } catch (e: Exception) {
            defaultLogger.verbose("youdao translate error", e)
            ""
        }


    }

    private fun sign(text: String, salt: Long): String = "fanyideskweb$text$salt$D".md5()
}