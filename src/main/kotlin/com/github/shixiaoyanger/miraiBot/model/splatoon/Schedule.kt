package com.github.shixiaoyanger.miraiBot.model.splatoon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Schedule(
        @SerialName("league")
        val leagueList: List<ScheduleItem>,
        @SerialName("gachi")
        val gachiList: List<ScheduleItem>,
        @SerialName("regular")
        val regularList: List<ScheduleItem>
) {
    @Serializable
    data class ScheduleItem(
            val id: Long,
            val rule: Rule,
            @SerialName("start_time") val startTime: Long,
            @SerialName("end_time") val endTime: Long,
            @SerialName("stage_a") val stageA: Stage,
            @SerialName("stage_b") val stageB: Stage,
            @SerialName("game_mode") val gameMode: GameMode

    ) {
        @Serializable
        data class Stage(
                val id: String? = null,
                val name: String,
                val image: String
        )

        @Serializable
        data class GameMode(
                val name: String,
                val key: String
        )

        @Serializable
        data class Rule(
                val name: String,
                @SerialName("multiline_name")
                val multilineName: String,
                val key: String
        )
    }
}

