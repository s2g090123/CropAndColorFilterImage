package com.itrustmachines.testcropimage.data

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import com.itrustmachines.testcropimage.utils.ImageOperation

open class ColorData(
    val filterType: ColorType,
    val hue: Int,
    val saturation: Int,
    val brightness: Int,
    val contrast: Int,
    val opacity: Int
) {
    fun buildColorMatrix(): ColorMatrix {
        return when (filterType) {
            ColorType.ADJUST -> {
                ColorMatrix(
                    ImageOperation.buildAdjustColorMatrix(
                        hue = hue.toFloat() * 1.8f,
                        saturation = saturation.toFloat(),
                        brightness = brightness.toFloat(),
                        contrast = contrast.toFloat(),
                        alpha = opacity / 100f
                    ).values
                )
            }
            ColorType.COLORIZE -> {
                ColorMatrix(
                    ImageOperation.buildColorizeColorMatrix(
                        hue = hue.toFloat(),
                        saturation = saturation / 100f,
                        brightness = brightness / 100f,
                        contrast = contrast.toFloat(),
                        alpha = opacity / 100f
                    ).values
                )
            }
            ColorType.FILL -> {
                ColorMatrix(
                    ImageOperation.buildFillColorMatrix(
                        hue = hue.toFloat(),
                        saturation = saturation / 100f,
                        brightness = brightness / 100f,
                        contrast = contrast.toFloat(),
                        alpha = opacity / 100f
                    ).values
                )
            }
        }
    }

    fun buildColorFilter(): ColorMatrixColorFilter {
        return ColorMatrixColorFilter(buildColorMatrix())
    }
}