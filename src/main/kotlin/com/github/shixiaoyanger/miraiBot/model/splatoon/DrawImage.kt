package com.github.shixiaoyanger.miraiBot.model.splatoon

import java.awt.Image

interface DrawImage {
    /**
     * 画涂地、真格、组排日程安排
     */
    fun drawSchedule(schedule: Schedule, idx: Int): Image

    /**
     * 画鲑鱼打工日程安排
     */
    fun drawCoopSchedule(schedules: CoopSchedules): Image

}