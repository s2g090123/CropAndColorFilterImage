package com.itrustmachines.testcropimage.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itrustmachines.testcropimage.ColorViewModel
import com.itrustmachines.testcropimage.R
import com.itrustmachines.testcropimage.data.ColorEditType
import com.itrustmachines.testcropimage.data.ColorType
import java.io.File
import kotlin.math.ceil
import kotlin.math.roundToInt

inline val String.color: Color get() = Color(android.graphics.Color.parseColor(this))

@Composable
fun ColorScreen(
    viewModel: ColorViewModel,
    imagePath: String
) {
    val isTouchSeekBar by remember { viewModel.isTouchSeekBar }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Top(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawCheckBoard(),
            viewModel = viewModel,
            imagePath = imagePath
        )
        Body(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState(), !isTouchSeekBar),
            viewModel = viewModel,
            image = imagePath
        )
        Bottom(
            modifier = Modifier
                .fillMaxWidth(),
            viewModel = viewModel,
            image = imagePath
        )
    }
}

@Composable
private fun Top(
    modifier: Modifier = Modifier,
    viewModel: ColorViewModel,
    imagePath: String
) {
    val colorMatrix by viewModel.colorMatrix.collectAsState()
    AsyncImage(
        modifier = modifier,
        model = File(imagePath),
        contentDescription = null,
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
    )
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    viewModel: ColorViewModel,
    image: String
) {
    val filterType by viewModel.filterType.collectAsState()
    val hueValue by viewModel.hueValue.collectAsState()
    val saturationValue by viewModel.saturationValue.collectAsState()
    val brightnessValue by viewModel.brightnessValue.collectAsState()
    val contrastValue by viewModel.contrastValue.collectAsState()
    val opacityValue by viewModel.opacityValue.collectAsState()
    var isShowPopup by remember { mutableStateOf(false) }
    var showColorPresent by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GradientColorSeekBar(
                modifier = Modifier.size(256.dp, 16.dp),
                isCursorVisible = false,
                startX = -hueValue.toFloat(),
                colors = if (filterType == ColorType.ADJUST) {
                    listOf(
                        Color.Red,
                        "#ff8000".color,
                        Color.Yellow,
                        "#80ff00".color,
                        Color.Green,
                        "#00ff80".color,
                        Color.Cyan,
                        "#0080ff".color,
                        Color.Blue,
                        "#8000ff".color,
                        "#ff00ff".color,
                        Color.Red
                    )
                } else {
                    listOf(
                        Color.hsv(
                            hueValue.toFloat(),
                            saturationValue / 100f,
                            brightnessValue / 100f
                        ),
                        Color.hsv(
                            hueValue.toFloat(),
                            saturationValue / 100f,
                            brightnessValue / 100f
                        )
                    )
                },
                fromValue = -100f,
                toValue = 100f,
                value = 0f,
                onValueChange = {},
                onTouchChange = {}
            )
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
                    .alpha(0.6f),
                onClick = { isShowPopup = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_black_more),
                    contentDescription = null
                )
                ColorPopup(
                    expand = isShowPopup,
                    onColorTypeUpdate = {
                        viewModel.updateColorType(it)
                    },
                    onPresentClick = { showColorPresent = true },
                    onResetClick = {
                        viewModel.reset()
                    },
                    onDismiss = { isShowPopup = false }
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GradientColorSeekBar(
                modifier = Modifier
                    .size(256.dp, 36.dp),
                colors = listOf(
                    Color.Red,
                    "#ff8000".color,
                    Color.Yellow,
                    "#80ff00".color,
                    Color.Green,
                    "#00ff80".color,
                    Color.Cyan,
                    "#0080ff".color,
                    Color.Blue,
                    "#8000ff".color,
                    "#ff00ff".color,
                    Color.Red
                ),
                fromValue = if (filterType == ColorType.ADJUST) -100f else 0f,
                toValue = if (filterType == ColorType.ADJUST) 100f else 360f,
                value = hueValue.toFloat(),
                onValueChange = { viewModel.updateHue(it.roundToInt()) },
                onTouchChange = { viewModel.isTouchSeekBar.value = it }
            )
            Box(
                modifier = Modifier
                    .size(56.dp, 36.dp)
                    .clickable { viewModel.updateEditColorType(ColorEditType.HUE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filterType == ColorType.ADJUST) "$hueValue%" else "$hueValue°",
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    color = "#de000000".color,
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GradientColorSeekBar(
                modifier = Modifier
                    .size(256.dp, 36.dp),
                colors = if (filterType == ColorType.ADJUST) {
                    listOf("#cccccc".color, "#ff0000".color)
                } else {
                    listOf(
                        Color.hsv(hueValue.toFloat(), 0f, 1f),
                        Color.hsv(hueValue.toFloat(), 1f, 1f)
                    )
                },
                fromValue = if (filterType == ColorType.ADJUST) -100f else 0f,
                toValue = 100f,
                value = saturationValue.toFloat(),
                onValueChange = { viewModel.updateSaturation(it.roundToInt()) },
                onTouchChange = { viewModel.isTouchSeekBar.value = it }
            )
            Box(
                modifier = Modifier
                    .size(56.dp, 36.dp)
                    .clickable { viewModel.updateEditColorType(ColorEditType.SATURATION) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$saturationValue%",
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    color = "#de000000".color,
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GradientColorSeekBar(
                modifier = Modifier
                    .size(256.dp, 36.dp),
                colors = if (filterType == ColorType.ADJUST) {
                    listOf("#000000".color, "#ffffff".color)
                } else {
                    listOf(
                        Color.hsv(hueValue.toFloat(), saturationValue / 100f, 0f),
                        Color.hsv(hueValue.toFloat(), saturationValue / 100f, 1f)
                    )
                },
                fromValue = if (filterType == ColorType.ADJUST) -100f else 0f,
                toValue = 100f,
                value = brightnessValue.toFloat(),
                onValueChange = { viewModel.updateBrightness(it.roundToInt()) },
                onTouchChange = { viewModel.isTouchSeekBar.value = it }
            )
            Box(
                modifier = Modifier
                    .size(56.dp, 36.dp)
                    .clickable { viewModel.updateEditColorType(ColorEditType.VALUE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$brightnessValue%",
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    color = "#de000000".color,
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                modifier = Modifier
                    .alpha(0.6f)
                    .size(24.dp),
                painter = painterResource(R.drawable.ic_black_contrast),
                contentDescription = null
            )
            CustomSlider(
                modifier = Modifier
                    .width(224.dp),
                value = contrastValue / 100f,
                valueRange = -1f..1f,
                onValueChange = { viewModel.updateContrast((it * 100).toInt()) }
            )
            Box(
                modifier = Modifier
                    .size(56.dp, 36.dp)
                    .clickable { viewModel.updateEditColorType(ColorEditType.CONTRAST) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$contrastValue%",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.25.sp,
                    color = "#de000000".color,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(330.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                modifier = Modifier
                    .alpha(0.6f)
                    .size(24.dp),
                painter = painterResource(R.drawable.ic_black_opacity),
                contentDescription = null
            )
            CustomSlider(
                modifier = Modifier
                    .width(224.dp),
                value = opacityValue / 100f,
                onValueChange = { viewModel.updateOpacity((it * 100f).toInt()) }
            )
            Box(
                modifier = Modifier
                    .size(56.dp, 36.dp)
                    .clickable { viewModel.updateEditColorType(ColorEditType.OPACITY) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$opacityValue%",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.25.sp,
                    color = "#de000000".color,
                )
            }
        }
    }
    if (showColorPresent) {
        ColorPresentDialog(
            image = image,
            onSelectPresent = { viewModel.applyColor(it) },
            onDismiss = { showColorPresent = false }
        )
    }
}

@Composable
private fun Bottom(
    modifier: Modifier = Modifier,
    viewModel: ColorViewModel,
    image: String
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .clickable {
                    viewModel.applyAndSave(image)
                }
                .padding(horizontal = 13.dp, vertical = 10.dp)
                .align(Alignment.BottomEnd),
            text = "SAVE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun Modifier.drawCheckBoard() = composed {
    val boxSize = with(LocalDensity.current) { 12.dp.toPx() }
    drawWithContent {
        val rowCount = ceil(size.height / boxSize).toInt()
        val columnCount = ceil(size.width / boxSize).toInt()
        repeat(rowCount) { row ->
            repeat(columnCount) { column ->
                val isGrayBox = (row + column) % 2 == 0
                val color = if (isGrayBox) Color(0x40000000) else Color.White
                drawRect(
                    color = color,
                    topLeft = Offset(column * boxSize, row * boxSize),
                    size = Size(boxSize, boxSize)
                )
            }
        }
        drawContent()
    }
}