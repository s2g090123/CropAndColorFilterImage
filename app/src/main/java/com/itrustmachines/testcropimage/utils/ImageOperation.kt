package com.itrustmachines.testcropimage.utils

import android.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object ImageOperation {
    /**
     * 核心邏輯來自： https://stackoverflow.com/a/15119089
     * 但明亮度的實作不同，其他方法亦作若干修正
     *
     * @param hue: [-180, 180]
     * @param saturation: [-100, 100]
     * @param brightness: [-100, 100]
     * @param contrast: [-100, 100]
     * @param alpha: [0, 1]
     */
    fun buildAdjustColorMatrix(
        hue: Float,
        saturation: Float,
        brightness: Float,
        contrast: Float,
        alpha: Float
    ): ColorMatrix {
        val colorMatrix = ColorMatrix().also {
            it.adjustOpacity(alpha)
            it.adjustHue(hue)
            it.adjustContrast(contrast)
            it.adjustBrightness(brightness)
            it.adjustSaturation(saturation)
        }
        return colorMatrix
    }

    /**
     * @param hue: [0, 360]
     * @param saturation: [0, 1]
     * @param brightness: [0, 1]
     * @param contrast: [-100, 100]
     * @param alpha: [0, 1]
     */
    fun buildColorizeColorMatrix(
        hue: Float,
        saturation: Float,
        brightness: Float,
        contrast: Float,
        alpha: Float
    ): ColorMatrix {
        val color = Color.HSVToColor(floatArrayOf(hue, saturation, brightness))
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix.postTimesAssign(
            ColorMatrix(
                floatArrayOf(
                    1 - red / 255f, 0f, 0f, 0f, red.toFloat(),
                    0f, 1 - green / 255f, 0f, 0f, green.toFloat(),
                    0f, 0f, 1 - blue / 255f, 0f, blue.toFloat(),
                    0f, 0f, 0f, alpha, 0f
                )
            )
        )
        colorMatrix.adjustContrast(contrast)
        return colorMatrix
    }

    /**
     * @param hue: [0, 360]
     * @param saturation: [0, 1]
     * @param brightness: [0, 1]
     * @param contrast: [-100, 100]
     * @param alpha: [0, 1]
     */
    fun buildFillColorMatrix(
        hue: Float,
        saturation: Float,
        brightness: Float,
        contrast: Float,
        alpha: Float
    ): ColorMatrix {
        val color = Color.HSVToColor(floatArrayOf(hue, saturation, brightness))
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                0f, 0f, 0f, 0f, red.toFloat(),
                0f, 0f, 0f, 0f, green.toFloat(),
                0f, 0f, 0f, 0f, blue.toFloat(),
                0f, 0f, 0f, alpha, 0f
            )
        )
        colorMatrix.adjustContrast(contrast)
        return colorMatrix
    }

    private fun ColorMatrix.adjustHue(hue: Float) {
        // 色相在正負PI之間
        val value = hue.coerceIn(-180f, 180f) / 180f * Math.PI
        // 數值為零表示與原圖相同，不須額外處理
        if (value == 0.0) {
            return
        }

        val cosVal = cos(value)
        val sinVal = sin(value)
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f

        val matrix = floatArrayOf(
            (lumR + cosVal * (1 - lumR) + sinVal * -lumR).toFloat(),
            (lumG + cosVal * -lumG + sinVal * -lumG).toFloat(),
            (lumB + cosVal * -lumB + sinVal * (1 - lumB)).toFloat(),
            0f,
            0f,
            (lumR + cosVal * -lumR + sinVal * 0.143f).toFloat(),
            (lumG + cosVal * (1 - lumG) + sinVal * 0.140f).toFloat(),
            (lumB + cosVal * -lumB + sinVal * -0.283f).toFloat(),
            0f,
            0f,
            (lumR + cosVal * -lumR + sinVal * -(1 - lumR)).toFloat(),
            (lumG + cosVal * -lumG + sinVal * lumG).toFloat(),
            (lumB + cosVal * (1 - lumB) + sinVal * lumB).toFloat(),
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f
        )

        postTimesAssign(ColorMatrix(matrix))
    }

    private fun ColorMatrix.adjustContrast(contrast: Float) {
        // 對比度在正負100之間
        val value = contrast.coerceIn(-100f, 100f).toInt()
        // 數值為零表示與原圖相同，不須額外處理
        if (value == 0) {
            return
        }

        val x = when (value < 0) {
            true -> value / 100f
            false -> {
                // 查表找值
                floatArrayOf(
                    0.00f, 0.01f, 0.02f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.10f, 0.11f,
                    0.12f, 0.14f, 0.15f, 0.16f, 0.17f, 0.18f, 0.20f, 0.21f, 0.22f, 0.24f,
                    0.25f, 0.27f, 0.28f, 0.30f, 0.32f, 0.34f, 0.36f, 0.38f, 0.40f, 0.42f,
                    0.44f, 0.46f, 0.48f, 0.50f, 0.53f, 0.56f, 0.59f, 0.62f, 0.65f, 0.68f,
                    0.71f, 0.74f, 0.77f, 0.80f, 0.83f, 0.86f, 0.89f, 0.92f, 0.95f, 0.98f,
                    1.00f, 1.06f, 1.12f, 1.18f, 1.24f, 1.30f, 1.36f, 1.42f, 1.48f, 1.54f,
                    1.60f, 1.66f, 1.72f, 1.78f, 1.84f, 1.90f, 1.96f, 2.00f, 2.12f, 2.25f,
                    2.37f, 2.50f, 2.62f, 2.75f, 2.87f, 3.00f, 3.20f, 3.40f, 3.60f, 3.80f,
                    4.00f, 4.30f, 4.70f, 4.90f, 5.00f, 5.50f, 6.00f, 6.50f, 6.80f, 7.00f,
                    7.30f, 7.50f, 7.80f, 8.00f, 8.40f, 8.70f, 9.00f, 9.40f, 9.60f, 9.80f, 10.0f
                ).let {
                    it[value]
                }
            }
        }

        // output = input * (1 + x) - 128 * x
        val matrix = floatArrayOf(
            1 + x, 0f, 0f, 0f, -128 * x,
            0f, 1 + x, 0f, 0f, -128 * x,
            0f, 0f, 1 + x, 0f, -128 * x,
            0f, 0f, 0f, 1f, 0f
        )
        postTimesAssign(ColorMatrix(matrix))
    }

    private fun ColorMatrix.adjustBrightness(brightness: Float) {
        // 明亮度在正負100之間
        val value = brightness.coerceIn(-100f, 100f)
        // 數值為零表示與原圖相同，不須額外處理
        if (value == 0f) {
            return
        }

        // 與中心點的距離，量化為0.0至1.0
        val ratio = abs(value / 100f)
        // 如果明亮度為正值：將顏色往0xffffff修正
        // 如果明亮度為負值：將顏色往0x000000修正
        val target = when (value > 0f) {
            true -> 255
            false -> 0
        }
        // output = input * (1 - ratio) + target * ratio
        val matrix = floatArrayOf(
            1 - ratio, 0f, 0f, 0f, target * ratio,
            0f, 1 - ratio, 0f, 0f, target * ratio,
            0f, 0f, 1 - ratio, 0f, target * ratio,
            0f, 0f, 0f, 1f, 0f
        )
        postTimesAssign(ColorMatrix(matrix))
    }

    private fun ColorMatrix.adjustSaturation(saturation: Float) {
        // 飽和度在正負100之間
        val value = saturation.coerceIn(-100f, 100f)
        // 數值為零表示與原圖相同，不須額外處理
        if (value == 0f) {
            return
        }

        // x介於0到4之間
        val x = 1 + if (value > 0) 3 * value / 100 else value / 100
        // 亮度（人眼對光線強度實際感受的物理量）色彩分量
        // 用來對圖片做灰階處理
        val lumR = 0.3086f
        val lumG = 0.6094f
        val lumB = 0.0820f

        // gray = inputR * lumR + inputG * lumG + inputB * lumB
        // output = gray * (1 - x) + input * x
        val matrix = floatArrayOf(
            lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0f, 0f,
            lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0f, 0f,
            lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        postTimesAssign(ColorMatrix(matrix))
    }

    private fun ColorMatrix.adjustOpacity(alpha: Float) {
        val value = alpha.coerceIn(0f, 1f)
        if (value == 1f) return

        val matrix = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, value, 0f
        )
        postTimesAssign(ColorMatrix(matrix))
    }

    private fun ColorMatrix.postTimesAssign(matrix: ColorMatrix) {
        val target = ColorMatrix(matrix.values)
        target.timesAssign(this)
        set(target)
    }
}