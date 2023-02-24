package com.itrustmachines.testcropimage.compose

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GradientColorSeekBar(
    modifier: Modifier = Modifier,
    startX: Float = 0f,
    isCursorVisible: Boolean = true,
    colors: List<Color>,
    fromValue: Float,
    toValue: Float,
    value: Float,
    onValueChange: (value: Float) -> Unit,
    onTouchChange: (isTouch: Boolean) -> Unit
) {
    require(fromValue <= toValue)
    var barWidth by remember { mutableStateOf(1) }
    val ratio by remember(fromValue, toValue) {
        derivedStateOf { abs(toValue - fromValue) / barWidth }
    }
    val cursorOffset by remember(value) {
        derivedStateOf { (value - fromValue) / ratio }
    }
    val onValueChanged by rememberUpdatedState(onValueChange)
    val onTouchChanged by rememberUpdatedState(onTouchChange)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = colors,
                        startX = startX / ratio,
                        endX = barWidth + startX / ratio,
                        tileMode = TileMode.Repeated
                    )
                )
                .border(1.dp, Color(0xff999999))
                .onSizeChanged { barWidth = it.width }
                .pointerInteropFilter {
                    onValueChanged((it.x * ratio + fromValue).coerceIn(fromValue, toValue))
                    if (it.action == MotionEvent.ACTION_DOWN) {
                        onTouchChanged(true)
                    } else if (it.action == MotionEvent.ACTION_UP || it.action == MotionEvent.ACTION_CANCEL) {
                        onTouchChanged(false)
                    }
                    true
                }
        )
        if (isCursorVisible) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = cursorOffset.roundToInt(), 0) }
                    .fillMaxHeight()
                    .width(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xff999999))
                    .background(Color.White)
            )
        }
    }
}