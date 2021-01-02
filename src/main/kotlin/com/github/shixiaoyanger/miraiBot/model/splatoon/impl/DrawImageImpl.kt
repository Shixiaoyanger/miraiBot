package com.github.shixiaoyanger.miraiBot.model.splatoon.impl

import cn.hutool.http.HttpUtil
import com.github.shixiaoyanger.miraiBot.model.splatoon.CoopSchedules
import com.github.shixiaoyanger.miraiBot.model.splatoon.CoopSchedules.CoopSchedule.Weapons
import com.github.shixiaoyanger.miraiBot.model.splatoon.DrawImage
import com.github.shixiaoyanger.miraiBot.model.splatoon.Schedule
import com.github.shixiaoyanger.miraiBot.model.splatoon.StageType
import com.github.shixiaoyanger.miraiBot.model.splatoon.consts.*
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.Format
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.difTimeToStr
import com.github.shixiaoyanger.miraiBot.utils.TimeUtils.getDateTime
import com.github.shixiaoyanger.miraiBot.utils.drawVerticalString
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class DrawImageImpl(width: Int, height: Int, baseColor: Color) : DrawImage {
    private val background: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private val g: Graphics2D

    data class Coordinate(
            var x: Int,
            var y: Int,
    )

    private val xy = Coordinate(0, 0)


    private val haiPaiFont: Font = getFont("/fonts/HaiPaiQiangDiaoGunShiJian-2.otf")
    private val paintballFont: Font = getFont("/fonts/Paintball_Beta_4a.otf")

    private var fontSize = 40f

    init {
        g = background.createGraphics()
        g.color = baseColor

        //填充背景
        g.fillRect(0, 0, width, height)
        g.drawRect(0, 0, width, height)

        //消除锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }

    override fun drawSchedule(schedule: Schedule, idx: Int): BufferedImage {
        val startTime = schedule.regularList[idx].startTime * 1000
        val endTime = schedule.regularList[idx].endTime * 1000

        drawMode(schedule.gachiList[idx], StageType.gachi)

        xy.y += StageHeight + 10
        drawMode(schedule.regularList[idx], StageType.regular)

        xy.y += StageHeight + 10
        drawMode(schedule.leagueList[idx], StageType.league)

        xy.y += StageHeight + 10
        xy.x += RuleWidth
        drawTime(startTime, endTime, Format.hm)

        return background
    }

    override fun drawCoopSchedule(schedules: CoopSchedules): BufferedImage {
        val (now, startTime, endTime) = listOf(System.currentTimeMillis(), schedules.details[0].startTime * 1000, schedules.details[0].endTime * 1000)
        val timeStr = if (now < startTime) {
            "In ${difTimeToStr(startTime - now)} !"
        } else {
            "${difTimeToStr(endTime - now)} Remaining !"
        }
        g.font = paintballFont.deriveFont(20f)
        g.color = Color.WHITE
        g.drawString(timeStr, xy.x + 10, xy.y + 25)
        xy.y += 15

        fontSize = 20f
        schedules.details.forEach {
            drawTime(it.startTime * 1000, it.endTime * 1000, Format.mdhm)

            xy.y += TextHeight
            drawCoopLine(it)

            xy.y += CoopStageHeight
        }

        return background
    }

    private fun drawMode(mode: Schedule.ScheduleItem, stageType: StageType) {
        val (x, y) = xy
        val ruleName = RuleTranslate[mode.rule.key]

        g.color = Color.WHITE
        g.font = haiPaiFont.deriveFont(40f)
        ruleName?.let {
            g.drawVerticalString(it, x + 10, y + 10)
        }

        val imageA = getImage(mode.stageA.image).getScaledInstance(StageWidth, StageHeight, Image.SCALE_SMOOTH)
        val imageB = getImage(mode.stageB.image).getScaledInstance(StageWidth, StageHeight, Image.SCALE_SMOOTH)


        g.drawImage(imageA, x + 5 + RuleWidth, y + 10, null)

        g.drawImage(imageB, x + 5 + RuleWidth + StageWidth + 10, y + 10, null)

        drawRuleIcon(stageType)

    }

    private fun drawRuleIcon(stageType: StageType) {
        val (x, y) = xy
        val iconPath = "/images/stage_types/${stageType.value}.png"
        val imgStream = object {}::class.java.getResourceAsStream(iconPath)
        val icon = ImageIO.read(imgStream).zoom(0.6)

        g.drawImage(icon, x + 10 + RuleWidth + StageWidth - icon.width / 2, y + StageHeight / 2 - icon.height / 2, null)
    }

    private fun drawTime(startTime: Long, endTime: Long, format: Format) {
        val (x, y) = xy
        val timeRange = "${getDateTime(startTime, format)} - ${getDateTime(endTime, format)}"
        g.color = Color.WHITE
        g.font = paintballFont.deriveFont(fontSize)
        g.drawString(timeRange, x + 10, y + 40)

    }

    private fun drawCoopLine(schedule: CoopSchedules.CoopSchedule) {
        val (x, y) = xy

        val image = getFixedImage(getImage(schedule.stage.image), CoopStageWidth, CoopStageHeight)
        g.drawImage(image, x + 10, y + 10, null)

        // 每个weapon的坐标
        val xys = listOf(
                Coordinate(x + CoopStageWidth + 10, y),
                Coordinate(x + CoopStageWidth + 10 + WeaponSize + 10, y),
                Coordinate(x + CoopStageWidth + 10, y + WeaponSize),
                Coordinate(x + CoopStageWidth + 10 + WeaponSize + 10, y + WeaponSize),
        )
        schedule.weapons.forEachIndexed { index, weapons ->
            drawWeapon(weapons, xys[index])
        }
    }

    private fun drawWeapon(weapons: Weapons, weaponCoordinate: Coordinate) {
        val (x, y) = weaponCoordinate
        val weapon = weapons.weapon ?: weapons.coopSpecialWeapon
        val imagePath = (weapon?.image) ?: "/images/coop_weapons/746f7e90bc151334f0bf0d2a1f0987e311b03736.png"
        val image = getFixedImage(getImage(imagePath), WeaponSize, WeaponSize)

        g.drawImage(image, x + 10, y + 10, null)
    }

    private fun getImage(path: String, prefix: String = "data"): BufferedImage {

        return ImageIO.read(
                File(prefix + path).apply {
                    if (!this.exists()) {
                        println("downloading: $path")
                        HttpUtil.downloadFile(appBaseURL + path, this)
                    }
                }
        )
    }

    private fun getFont(path: String): Font {
        val fontStream = object {}::class.java.getResourceAsStream(path)
        return Font.createFont(Font.TRUETYPE_FONT, fontStream)
    }


    private fun BufferedImage.zoom(zoomRate: Double): BufferedImage {

        val width = (this.width * zoomRate).toInt()
        val height = (this.height * zoomRate).toInt()

        return getFixedImage(this, width, height)
    }

    private fun getFixedImage(image: BufferedImage, width: Int, height: Int): BufferedImage {

        val scaledImage: Image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)

        val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        val g = result.graphics
        g.drawImage(scaledImage, 0, 0, null)

        return result
    }
}