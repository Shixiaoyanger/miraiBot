package com.github.shixiaoyanger.miraiBot.model.splatoon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CoopSchedules(
        val details: List<CoopSchedule>
) {
    @Serializable
    data class CoopSchedule(
            @SerialName("stage") val stage: Schedule.ScheduleItem.Stage,
            @SerialName("start_time") val startTime: Long,
            @SerialName("end_time") val endTime: Long,
            @SerialName("weapons") val weapons: List<Weapons>
    ) {
        @Serializable
        data class Weapons(
                val id: String,
                @SerialName("weapon")
                val weapon: Weapon? = null,
                @SerialName("coop_special_weapon")
                val coopSpecialWeapon: Weapon? = null
        ) {
            @Serializable
            data class Weapon(
                    val id: String = "-1",
                    val name: String,
                    val image: String,
                    val thumbnail: String = ""
            )
        }

    }
}


