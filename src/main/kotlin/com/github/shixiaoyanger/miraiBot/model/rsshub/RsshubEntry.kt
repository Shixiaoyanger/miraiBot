package com.github.shixiaoyanger.miraiBot.model.rsshub

import kotlinx.serialization.Serializable

@Serializable
data class RsshubEntry(
        val title: String,
        val description: String,
        val imageUrl: String?,
        val pubDate: Long,
        //The link/URI is the unique identifier, in the RSS 2.0/atom case this is the GUID.
        val link: String
)