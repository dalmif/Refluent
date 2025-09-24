package io.kayt.refluent.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    thickness: Dp = 1.dp,
    dashWidth: Dp = 8.dp,
    gapWidth: Dp = 4.dp
) {
    val stroke = with(LocalDensity.current) {
        Stroke(
            width = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashWidth.toPx(), gapWidth.toPx()), 0f
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = stroke.width,
            pathEffect = stroke.pathEffect
        )
    }
}