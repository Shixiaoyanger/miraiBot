package com.github.shixiaoyanger.miraiBot.bot

import kotlinx.serialization.Serializable

/**
 *@description 存放QQ账号信息
 *
 *@author yanger
 *
 *@create 2020-11-12
 **/
@Serializable
class User(val id: Long) {
    val rsshubSubscribeLinks: MutableSet<String> = mutableSetOf()

    companion object {
        fun getUser(id: Long): User = BotData.users[id] ?: createUser(id)
        fun createUser(id: Long): User = User(id).also { BotData.users.set(id, it) }
    }
}