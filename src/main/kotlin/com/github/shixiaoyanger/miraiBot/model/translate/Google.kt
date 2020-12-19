package com.github.shixiaoyanger.miraiBot.model.translate

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.utils.json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// 谷歌翻译
object Google {
    private val host = "http://translate.google.cn/"

    // 这样调用google翻译有流量限制，请求过快会被识别为机器block，这时会用有道翻译做备选
    fun translate(text: String, from: Language = Language.AUTO, to: Language = Language.AUTO): String {

        val param = "translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=auto&tl=$to&q=$text"

        return try {
            val result = HttpUtil.get(host + param)
            json.parseToJsonElement(result).jsonObject["sentences"]!!.jsonArray[0].jsonObject["trans"]!!.jsonPrimitive.content
        } catch (e: Exception) {
            Youdao.translate(text, from, to)
        }
    }


}