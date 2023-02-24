package com.itrustmachines.testcropimage.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    /*@IntRange(from = 0)*/
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = "#ffb600".color,
        activeTrackColor = "#ffb600".color,
        inactiveTrackColor = "#42ffb600".color
    )
) {
    val seekValue by remember(value) { mutableStateOf(value) }
    var dragValue by remember { mutableStateOf(value) }
    val isDrag by interactionSource.collectIsDraggedAsState()

    Slider(
        modifier = modifier,
        value = if (isDrag) dragValue else seekValue,
        onValueChange = {
            dragValue = it
            onValueChange(it)
        },
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        colors = colors
    )
}