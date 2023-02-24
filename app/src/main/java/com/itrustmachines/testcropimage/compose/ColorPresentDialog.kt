package com.itrustmachines.testcropimage.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.itrustmachines.testcropimage.data.*
import java.io.File

@Composable
fun ColorPresentDialog(
    image: String,
    onSelectPresent: (colorFilterData: ColorData) -> Unit,
    onDismiss: () -> Unit
) {
    val filterTypeArray by rememberSaveable {
        mutableStateOf(
            arrayOf(
                "Original" to OriginalColorData(),
                "Invert" to InvertColorData(),
                "Desaturate" to DesaturateColorData(),
                "Cyanotype" to CyanotypeColorData(),
                "Old Style" to OldStyleColorData(),
                "Silhouette" to SilhouetteColorData(),
                "Strong Saturation" to StrongSaturationColorData(),
                "Yellowing" to YellowingColorData()
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .heightIn(0.dp, 452.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            item {
                Text(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp
                    ),
                    text = "Presents",
                    letterSpacing = 0.15.sp,
                    fontSize = 20.sp,
                    color = "#de000000".color,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium
                )
            }
            items(filterTypeArray) {
                ColorPresentItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { onSelectPresent(it.second) }
                        .padding(bottom = 8.dp),
                    image = image,
                    colorFilterData = it.second,
                    name = it.first
                )
            }
        }
    }
}

@Composable
private fun ColorPresentItem(
    modifier: Modifier = Modifier,
    image: String,
    colorFilterData: ColorData,
    name: String
) {
    ConstraintLayout(modifier = modifier) {
        val (thumbnail, nameText) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(thumbnail) {
                    linkTo(parent.top, parent.bottom)
                    start.linkTo(parent.start, 16.dp)
                }
                .size(56.dp)
                .border(1.dp, "#333333".color, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .drawCheckBoard()
            )
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                model = File(image),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(colorFilterData.buildColorMatrix().array)
                )
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(nameText) {
                    linkTo(thumbnail.end, parent.end, 16.dp, 16.dp)
                    linkTo(thumbnail.top, thumbnail.bottom)
                    width = Dimension.fillToConstraints
                },
            text = name,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            color = "#000000".color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}