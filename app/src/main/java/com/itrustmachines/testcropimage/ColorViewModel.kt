package com.itrustmachines.testcropimage

import android.graphics.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itrustmachines.testcropimage.data.ColorData
import com.itrustmachines.testcropimage.data.ColorEditType
import com.itrustmachines.testcropimage.data.ColorType
import com.itrustmachines.testcropimage.utils.ImageOperation
import kotlinx.coroutines.flow.*

class ColorViewModel : ViewModel() {

    // 用於當前使用SeekBar時，取消ScrollView的滑動事件
    val isTouchSeekBar = mutableStateOf(false)

    private val saveToDownloadImp = MutableStateFlow<Bitmap?>(null)
    val saveToDownload = saveToDownloadImp.asStateFlow()

    private val colorEditTypeImp = MutableStateFlow<ColorEditType?>(null)
    val colorEditType = colorEditTypeImp.asStateFlow()

    // [0, 360] or [-100, 100]
    private val hueValueImp = MutableStateFlow(0)
    val hueValue = hueValueImp.asStateFlow()

    // [0, 100] or [-100, 100]
    private val saturationValueImp = MutableStateFlow(0)
    val saturationValue = saturationValueImp.asStateFlow()

    // [0, 100] or [-100, 100]
    private val brightnessValueImp = MutableStateFlow(0)
    val brightnessValue = brightnessValueImp.asStateFlow()

    // [-100, 100]
    private val contrastValueImp = MutableStateFlow(0)
    val contrastValue = contrastValueImp.asStateFlow()

    // [0, 100]
    private val opacityValueImp = MutableStateFlow(100)
    val opacityValue = opacityValueImp.asStateFlow()

    private val filterTypeImp = MutableStateFlow(ColorType.ADJUST)
    val filterType = filterTypeImp.asStateFlow()

    private val contrast2Opacity = combine(contrastValue, opacityValue) { contrast, opacity ->
        contrast to opacity
    }

    val colorMatrix = combine(
        filterType, hueValue, saturationValue, brightnessValue, contrast2Opacity
    ) { filterType, hue, saturation, brightness, contrast2Opacity ->
        val contrast = contrast2Opacity.first
        val opacity = contrast2Opacity.second
        when (filterType) {
            ColorType.FILL -> {
                ImageOperation.buildFillColorMatrix(
                    hue.toFloat(),
                    saturation / 100f,
                    brightness / 100f,
                    contrast.toFloat(),
                    opacity / 100f
                )
            }
            ColorType.COLORIZE -> {
                ImageOperation.buildColorizeColorMatrix(
                    hue.toFloat(),
                    saturation / 100f,
                    brightness / 100f,
                    contrast.toFloat(),
                    opacity / 100f
                )
            }
            else -> {
                ImageOperation.buildAdjustColorMatrix(
                    hue.toFloat() * 1.8f,
                    saturation.toFloat(),
                    brightness.toFloat(),
                    contrast.toFloat(),
                    opacity / 100f
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ColorMatrix())

    fun updateColorType(type: ColorType) {
        if (type == filterType.value) return
        val currentType = filterType.value
        filterTypeImp.value = type
        if (type == ColorType.ADJUST) {
            updateHue(0)
            updateSaturation(0)
            updateBrightness(0)
        } else if (currentType == ColorType.ADJUST) {
            updateHue(180)
            updateSaturation(50)
            updateBrightness(50)
        }
    }

    fun updateHue(value: Int) {
        val hue = if (filterType.value != ColorType.ADJUST) {
            value.coerceIn(0, 360)
        } else {
            value.coerceIn(-100, 100)
        }
        hueValueImp.value = hue
    }

    fun updateSaturation(value: Int) {
        val saturation = if (filterType.value != ColorType.ADJUST) {
            value.coerceIn(0, 100)
        } else {
            value.coerceIn(-100, 100)
        }
        saturationValueImp.value = saturation
    }

    fun updateBrightness(value: Int) {
        val brightness = if (filterType.value != ColorType.ADJUST) {
            value.coerceIn(0, 100)
        } else {
            value.coerceIn(-100, 100)
        }
        brightnessValueImp.value = brightness
    }

    fun updateContrast(value: Int) {
        contrastValueImp.value = value.coerceIn(-100, 100)
    }

    fun updateOpacity(value: Int) {
        opacityValueImp.value = value.coerceIn(0, 100)
    }

    fun reset() {
        filterTypeImp.value = ColorType.ADJUST
        updateHue(0)
        updateSaturation(0)
        updateBrightness(0)
        updateContrast(0)
        updateOpacity(100)
    }

    fun getColorData(): ColorData {
        return ColorData(
            filterType = filterType.value,
            hue = hueValue.value,
            saturation = saturationValue.value,
            brightness = brightnessValue.value,
            contrast = contrastValue.value,
            opacity = opacityValue.value
        )
    }

    fun applyColor(colorFilterData: ColorData) {
        updateColorType(colorFilterData.filterType)
        updateHue(colorFilterData.hue)
        updateSaturation(colorFilterData.saturation)
        updateBrightness(colorFilterData.brightness)
        updateContrast(colorFilterData.contrast)
        updateOpacity(colorFilterData.opacity)
    }

    fun updateEditColorType(type: ColorEditType?) {
        colorEditTypeImp.value = type
    }

    fun applyAndSave(image: String) {
        val bitmap = BitmapFactory.decodeFile(image)
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Canvas(output).apply {
            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(
                    android.graphics.ColorMatrix(colorMatrix.value.values)
                )
            }
            drawBitmap(bitmap, 0f, 0f, paint)
        }
        saveToDownloadImp.value = output
    }
}