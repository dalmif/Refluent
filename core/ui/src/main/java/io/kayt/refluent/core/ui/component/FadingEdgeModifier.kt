package io.kayt.refluent.core.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.lang.Float
import java.lang.Float.min

fun Modifier.fadingEdges(
    listState: LazyListState,
    topEdgeHeight: Dp = 72.dp,
    bottomEdgeHeight: Dp = 72.dp,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val colors = listOf(Color.Black, Color.Transparent)
        val topGradientHeight =
            Float.min(topEdgeHeight.toPx(), listState.firstVisibleItemScrollOffset.toFloat())
        if (topGradientHeight != 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = 0f,
                    endY = topGradientHeight
                ),
                blendMode = BlendMode.DstOut
            )
        }
        val bottomEdgeHeightPx = bottomEdgeHeight.toPx()
        val bottomGradientHeight = let {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            if (lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1) {
                val lastVisibleItemScrollOffset = lastVisibleItem.offset
                val lastItemSize = lastVisibleItem.size
                val overflowHeight = lastVisibleItemScrollOffset + lastItemSize - size.height
                if (lastItemSize >= bottomEdgeHeightPx) {
                    Float.min(bottomEdgeHeight.toPx(), overflowHeight)
                } else {
                    (overflowHeight / lastItemSize) * bottomEdgeHeightPx
                }
            } else {
                bottomEdgeHeightPx
            }
        }
        if (bottomGradientHeight != 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = size.height,
                    endY = size.height - bottomGradientHeight
                ),
                blendMode = BlendMode.DstOut
            )
        }
    }

fun Modifier.fadingEdges(
    scrollState: ScrollState,
    topEdgeHeight: Dp = 72.dp,
    bottomEdgeHeight: Dp = 72.dp,
): Modifier = this.then(
    Modifier
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()

            val topColors = listOf(Color.Transparent, Color.Black)
            val topStartY = scrollState.value.toFloat()
            val topGradientHeight = min(topEdgeHeight.toPx(), topStartY)
            if (topGradientHeight != 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = topColors,
                        startY = topStartY,
                        endY = topStartY + topGradientHeight
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            val bottomColors = listOf(Color.Black, Color.Transparent)
            val bottomEndY = size.height - scrollState.maxValue + scrollState.value
            val bottomGradientHeight =
                min(bottomEdgeHeight.toPx(), scrollState.maxValue.toFloat() - scrollState.value)
            if (bottomGradientHeight != 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = bottomColors,
                        startY = bottomEndY - bottomGradientHeight,
                        endY = bottomEndY
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
        }
)
