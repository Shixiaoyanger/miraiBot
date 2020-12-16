package com.github.shixiaoyanger.miraiBot.utils

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.bot.BotData.config
import com.github.shixiaoyanger.miraiBot.bot.BotData.defaultLogger
import com.github.shixiaoyanger.miraiBot.bot.BotData.serviceLogger
import com.github.shixiaoyanger.miraiBot.model.imageSearch.ImageSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import kotlin.random.Random

object ImageSearchUtil {
    private const val sauceNaoUrl = "https://saucenao.com/search.php"
    private const val konachanUrl = "https://konachan.net/post.json"
    private const val imageKeyUrl = "http://konachan.wjcodes.com/FindTag.php"
    private val imageCache = ConcurrentHashMap<Rating, MutableSet<File>>()


    fun sauceNaoSearch(imgUrl: String): ImageSearchResult {
        lateinit var data: String
        try {
            // 构造post表单
            val param = "url=$imgUrl&api_key=${config.imageSearchKey.sauceNao}&output_type=2&numres=3"

            data = HttpUtil.post(sauceNaoUrl, param)

            // 从返回的json数据中解析结果
            val dataObj = Json.parseToJsonElement(data).jsonObject
            val header = dataObj["header"]
            val results = dataObj["results"]
            val result = results?.jsonArray?.get(0)?.jsonObject

            return if (header != null && result != null) {
                val shortLimit = header.jsonObject["short_limit"]!!.jsonPrimitive.int
                val longLimit = header.jsonObject["long_limit"]!!.jsonPrimitive.int

                val similarity = result["header"]!!.jsonObject["similarity"]?.jsonPrimitive?.double ?: -1.0
                val thumbnail = result["header"]!!.jsonObject["thumbnail"]!!.jsonPrimitive.content
                val extUrl = result["data"]!!.jsonObject["ext_urls"]!!.jsonArray[0].jsonPrimitive.content
                val title = result["data"]!!.jsonObject["title"]?.jsonPrimitive?.content

                ImageSearchResult(similarity, title, originUrl = extUrl, microUrl = thumbnail)
            } else {
                ImageSearchResult(-1.0, null, "", "")
            }
        } catch (e: Exception) {
            serviceLogger.warning("sauceNaoSearch failed. imageUrl = $imgUrl data = $data", e)
            return ImageSearchResult(-1.0, null, "", "")
        }
    }

    /**
     * 图片分级
     */
    enum class Rating {
        SAFE, //全年龄
        QUESTIONABLE,  // 15+
        EXPLICIT // 18+
    }

    fun getKonachanImg(bound: Int = 100, tags: String = "", rating: Rating = Rating.SAFE): BufferedImage? {
        defaultLogger.info("${imageCache[rating]}")
        var imageSet = imageCache[rating]
        if (imageSet == null) {
            imageSet = mutableSetOf()
            imageCache[rating] = imageSet
        }
        // 当标签为空且有图片缓存时，直接从缓存中取
        if (tags == "" && imageSet.isNotEmpty()) {
            val result = imageSet.last()
            imageSet.remove(result)
            defaultLogger.info("use image cache, imageSet size = ${imageCache[rating]?.size ?: -1}")
            return ImageIO.read(result)
        }
        try {
            val page = Random.nextInt(bound)

            val param = "limit=100&page=$page&tags=$tags%20rating:$rating".toLowerCase()
            val data = HttpUtil.post(konachanUrl, param) ?: return null

            val jsonArray = Json.parseToJsonElement(data).jsonArray
            if (jsonArray.isEmpty() && bound > 0) {
                return getKonachanImg(bound / 2, rating = rating)
            }

            val indexes = List(10) { Random.nextInt(0, jsonArray.size) }.toSet()
            val url = jsonArray[indexes.first()].jsonObject["sample_url"]?.jsonPrimitive?.content

            if (tags == "") {
                // 异步缓存标签为空的图片
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        indexes.drop(0).forEach {
                            val sampleUrl = jsonArray[it].jsonObject["sample_url"]!!.jsonPrimitive.content
                            val md5 = jsonArray[it].jsonObject["md5"]?.jsonPrimitive?.content ?: sampleUrl
                            val f = HttpUtil.downloadFileFromUrl(sampleUrl, File.createTempFile(md5, ".jpg"))
                            imageSet.add(f)
                        }
                        imageCache[rating]?.addAll(imageSet)
                        defaultLogger.info("image cached, imageSet size = ${imageCache[rating]?.size ?: -1}")
                    }
                }
            }
            // 返回本次结果
            return RssUtil.getImage(url)

        } catch (e: Exception) {
            serviceLogger.warning("getKonachanImg error", e)
            return null
        }

    }

    // tag 搜图效果不好，暂时不用
    fun getImageTag(key: String): String {
        val data = HttpUtil.post(imageKeyUrl, "key=$key")
        val jsonArray = Json.parseToJsonElement(data).jsonArray
        var result = ""
        jsonArray.forEach { element ->
            element.jsonObject["tag"]?.jsonPrimitive?.content?.let { result += "%20$it" }
        }
        return result
    }
}