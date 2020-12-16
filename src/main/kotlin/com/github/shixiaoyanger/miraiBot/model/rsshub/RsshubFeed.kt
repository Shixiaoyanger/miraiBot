package com.github.shixiaoyanger.miraiBot.model.rsshub


import kotlinx.serialization.Serializable


@Serializable
data class RsshubFeed(

        var subscribeUrl: String,
        var title: String,
        var link: String,
        var description: String,
        var lastBuildDate: Long,

        var itemLinks: List<String>,
        //@Transient
        var items: List<RsshubEntry>

)


