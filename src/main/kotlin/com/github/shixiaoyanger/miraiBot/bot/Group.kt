package com.github.shixiaoyanger.miraiBot.bot

import kotlinx.serialization.Serializable

@Serializable
class Group(val id: Long) {

    val rsshubSubscribeLinks: MutableSet<String> = mutableSetOf()

    companion object {
        fun getGroup(id: Long): Group = BotData.groups[id] ?: createGroup(id)

        fun createGroup(id: Long): Group = Group(id).also { BotData.groups[id] = it }
    }
}