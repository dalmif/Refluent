package io.kayt.refluent.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Help to make object from MeshGradient,
 *
 * @param width the number of point in X axis
 * @param height the number of point in Y axis
 * @param points all the gradient point with their color, each point needs an offset and the offset
 *  is relative to the width and height of composable that MeshGradient is applied to.
 *
 *  @sample MeshGradientPreview
 */
@Suppress("FunctionNaming")
fun MeshGradient(
    width: Int,
    height: Int,
    points: PointCollectorDsl.() -> Unit
): MeshGradientContext {
    val collector = PointCollectorDsl()
    return MeshGradientContext(
        width = width,
        height = height,
        points = {
            // recycle the collector to clean the previous points before adding new points
            collector.recycle()
            collector.points()
            collector.points
        }
    )
}

data class GradientPoint(
    val x: Float,
    val y: Float,
    val color: Color,
    val scaleX: Float,
    val scaleY: Float
)

class PointCollectorDsl() {
    internal val points = mutableListOf<GradientPoint>()
    fun point(x: Float, y: Float, color: Color, scale: Float = 1f) {
        points.add(GradientPoint(x, y, color, scale, scale))
    }

    fun point(x: Float, y: Float, color: Color, scaleX: Float = 1f, scaleY: Float = 1f) {
        points.add(GradientPoint(x, y, color, scaleX, scaleY))
    }

    fun recycle() {
        points.clear()
    }
}

data class MeshGradientContext(
    val width: Int,
    val height: Int,
    val points: () -> List<GradientPoint>
)

private fun Modifier.background(mesh: MeshGradientContext): Modifier {
    return this.drawBehind {
        val widthSpot = size.width / mesh.width
        val heightSpot = size.height / mesh.height
        val defaultScale = 1.6f
        val radius = widthSpot * defaultScale

        // We are using points as lambda to be able to read it in draw scope (here)
        // so that it's able to track the state changes inside the lambda
        // for example animating points using mutableStates
        val points = mesh.points()
        points.forEach { point ->
            val centerX = size.width * point.x
            val centerY = size.height * point.y
            val center = Offset(centerX, centerY)
            val color = point.color
            drawContext.canvas.withSave {
                scale(point.scaleX, (heightSpot / widthSpot) * point.scaleY, pivot = center) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to color,
                            1f to Color.Transparent,
                            radius = radius,
                            center = center
                        ),
                        center = center,
                        radius = radius
                    )
                }
            }
        }
    }
}

@Composable
fun MeshGradient(
    mesh: MeshGradientContext,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(modifier) {
        Box(
            Modifier
                .matchParentSize()
                // Blur causes the gradient to fade at the corners. To mitigate this, scale
                // the view by 10% to ensure the edges are fully covered and the fading is less noticeable
                .scale(1.1f)
                .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(mesh)
        )
        content()
    }
}

@Preview
@Composable
private fun MeshGradientPreview() {
    MeshGradient(
        modifier = Modifier.fillMaxSize(),
        mesh = MeshGradient(
            width = 3,
            height = 4,
            points = {
                point(0f, 0f, Color.Red)
                point(0.5f, 0f, Color(0xFF0055FF), scaleY = 1.4f)
                point(1f, 0f, Color(0xFF34C759))

                point(0f, 1f, Color(0xFF30B0C7))
                point(0.5f, 1f, Color(0xFFFF9500), scaleY = 1.4f)
                point(1f, 1f, Color(0xFF9878CB))
            }
        )
    ) {
        Text(
            "Hello Mesh Gradient",
            style = TextStyle(
                fontWeight = FontWeight.ExtraLight
            ),
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun MeshGradientWithAnimationPreview() {
    val state by animateOffsetOnBorder(3000)
    MeshGradient(
        modifier = Modifier.fillMaxSize(),
        mesh = MeshGradient(
            width = 3,
            height = 5,
            points = {
                point(state.x, state.y, Color.Red)
                point(1 - state.x, 1 - state.y, Color(0xFF0055FF), scaleY = 1.4f)
                point(abs(state.x - 1), state.y, Color(0xFF34C759))

                point(state.x, abs(state.y - 1), Color(0xFF30B0C7))
                point(0.5f, state.y, Color(0xFFFF9500), scaleY = 1.4f)
                point(0.5f, 1 - state.y, Color(0xFF9878CB))
            }
        )
    ) {
        Text(
            "Hello Mesh Gradient",
            style = TextStyle(
                fontWeight = FontWeight.ExtraLight
            ),
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun animateOffsetOnBorder(durationMillis: Int): State<Offset> {
    val progress = remember { Animatable(0f) }
    return produceState(Offset.Zero) {
        while (true) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                )
            ) {
                val animationValue = this@animateTo.value
                val normalizedX: Float
                val normalizedY: Float

                when {
                    animationValue <= 0.25f -> {
                        normalizedX = animationValue * 4
                        normalizedY = 0f
                    }

                    animationValue <= 0.5f -> {
                        normalizedX = 1f
                        normalizedY = (animationValue - 0.25f) * 4
                    }

                    animationValue <= 0.75f -> {
                        normalizedX = 1f - (animationValue - 0.5f) * 4
                        normalizedY = 1f
                    }

                    else -> {
                        normalizedX = 0f
                        normalizedY = 1f - (animationValue - 0.75f) * 4
                    }
                }
                this@produceState.value = Offset(normalizedX, normalizedY)
            }
            progress.snapTo(0f)
        }
    }
}
