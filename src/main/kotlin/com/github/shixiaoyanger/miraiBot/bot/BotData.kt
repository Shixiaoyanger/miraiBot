package com.github.shixiaoyanger.miraiBot.bot

import com.github.shixiaoyanger.miraiBot.model.rsshub.RsshubFeed
import com.github.shixiaoyanger.miraiBot.utils.FileUtil.getDictionary
import com.github.shixiaoyanger.miraiBot.utils.loadObject
import com.github.shixiaoyanger.miraiBot.utils.readContent
import com.github.shixiaoyanger.miraiBot.utils.writeAppend
import com.github.shixiaoyanger.miraiBot.utils.writeJson
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.PlatformLogger
import net.mamoe.yamlkt.Yaml
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object BotData {

    /**
     * 机器人配置
     */
    var config: Config = loadConfig()

    // region database

    /**
     * 群数据   并发不安全，影响应该不大？
     */
    var groups: MutableMap<Long, Group> = mutableMapOf()

    /**
     * 用户数据
     */
    var users: MutableMap<Long, User> = mutableMapOf()

    /**
     * rss 数据
     */
    var rsshubFeeds: MutableMap<String, RsshubFeed> = mutableMapOf()

    /**
     * splatoon日程缓存
     */
    var splatoonCache: SplatoonData? = null

    class SplatoonData(val data: String, val expire: Long)
    //endregion database

    //region log

    /**
     * 日志文件
     */
    lateinit var log: File

    /**
     * 基础日志文件
     */
    lateinit var defaultLog: File

    /**
     * 自定义bot基础日志
     */
    val defaultLogger: PlatformLogger = PlatformLogger("BotService", {
        defaultLog.writeAppend(it + "\n")
        println(it)
    })

    /**
     * 服务日志
     */
    val serviceLogger: PlatformLogger = PlatformLogger("Service", {
        log.writeAppend(it + "\n")
        defaultLog.writeAppend(it + "\n")
        println(it)
    })

    // endregion log

    /**
     * 定时任务线程池 默认 poolSize = 4
     */
    val scheduledPool: ScheduledExecutorService = Executors.newScheduledThreadPool(config.threadPoolSize)

    /**
     * 全局json解析器
     */
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val dataDir = getDictionary("data")
    private val usersFile = File(dataDir, "users.json")
    private val groupsFile = File(dataDir, "groups.json")
    private val rsshubFeedsFile = File(dataDir, "rsshubFeeds.json")

    fun saveData() {
        if (users.isNotEmpty()) usersFile.writeJson(users)
        if (groups.isNotEmpty()) groupsFile.writeJson(groups)
        if (rsshubFeeds.isNotEmpty()) rsshubFeedsFile.writeJson(rsshubFeeds)
        saveConfig()
    }

    /**
     * 从数据文件中加载数据
     */
    fun loadData() {
        groups = groupsFile.loadObject()
        serviceLogger.info("groups群数据已加载${groups.size}个")

        users = usersFile.loadObject()
        serviceLogger.info("users用户数据已加载${users.size}个")

        rsshubFeeds = rsshubFeedsFile.loadObject()
        serviceLogger.info("rsshubFeeds rss订阅数据已加载${rsshubFeeds.size}个")
    }

    /**
     * 加载机器人配置
     */
    private fun loadConfig(): Config {
        val data = File("config.yml").readContent()
        return Yaml.nonStrict.decodeFromString(Config.serializer(), data)
    }

    private fun saveConfig() {
        val data = Yaml.default.encodeToString(config)
        File("config.yml").writeText(data)
    }


}