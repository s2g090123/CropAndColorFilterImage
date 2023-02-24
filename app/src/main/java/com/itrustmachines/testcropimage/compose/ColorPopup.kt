package com.itrustmachines.testcropimage.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itrustmachines.testcropimage.data.ColorType

@Composable
fun ColorPopup(
    expand: Boolean,
    onColorTypeUpdate: (type: ColorType) -> Unit,
    onPresentClick: () -> Unit,
    onResetClick: () -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expand,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            onClick = {
                onColorTypeUpdate(ColorType.ADJUST)
                onDismiss()
            }
        ) {
            Text(
                text = "Adjust",
                fontSize = 16.sp,
                color = "#de000000".color,
                letterSpacing = 0.5.sp
            )
        }
        DropdownMenuItem(
            onClick = {
                onColorTypeUpdate(ColorType.COLORIZE)
                onDismiss()
            }
        ) {
            Text(
                text = "Colorize",
                fontSize = 16.sp,
                color = "#de000000".color,
                letterSpacing = 0.5.sp
            )
        }
        DropdownMenuItem(
            onClick = {
                onColorTypeUpdate(ColorType.FILL)
                onDismiss()
            }
        ) {
            Text(
                text = "Fill",
                fontSize = 16.sp,
                color = "#de000000".color,
                letterSpacing = 0.5.sp
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = "#1e000000".color
        )
        DropdownMenuItem(
            onClick = {
                onPresentClick()
                onDismiss()
            }
        ) {
            Text(
                text = "Presents",
                fontSize = 16.sp,
                color = "#de000000".color,
                letterSpacing = 0.5.sp
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = "#1e000000".color
        )
        DropdownMenuItem(
            onClick = {
                onResetClick()
                onDismiss()
            }
        ) {
            Text(
                text = "Reset",
                fontSize = 16.sp,
                color = "#de000000".color,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
        }
    }
}