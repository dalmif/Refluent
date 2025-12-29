package io.kayt.refluent.core.ui.component.button

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.theme.AppTheme

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier
            .height(61.dp)
            .clip(CircleShape)
            .border(
                1.dp,
                color = if (!AppTheme.isDark) Color.Black else AppTheme.colors.textSecondary.copy(
                    0.3f
                ),
                CircleShape
            ),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (!AppTheme.isDark) AppTheme.colors.background else Color.Transparent
        ),
        onClick = onClick,
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides AppTheme.typography.button,
                LocalContentColor provides
                        if (!AppTheme.isDark) AppTheme.colors.textPrimary
                        else AppTheme.colors.textSecondary
            ) {
                content()
            }
        }
    )
}

@Composable
fun SecondaryBigButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = AppTheme.colors.background,
    showDropShadow: Boolean = true,
    height: Dp = 61.dp,
    content: @Composable RowScope.() -> Unit,
) {
    val borderColor = if (!AppTheme.isDark) Color.Black else Color(0xFFCCCCCD)
    Button(
        modifier = modifier
            .height(height)
            .applyIf(showDropShadow) {
                dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 0.dp,
                        color = borderColor,
                        spread = 0.dp,
                        offset = DpOffset(0.dp, 1.dp),
                    )
                )
            }
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = background
        ),
        onClick = onClick,
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides AppTheme.typography.button,
                LocalContentColor provides
                        if (!AppTheme.isDark) AppTheme.colors.textPrimary
                        else AppTheme.colors.textSecondary
            ) {
                content()
            }
        }
    )
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    SecondaryButton(
        onClick = {},
        content = {
            Text("Add Card")
        }
    )
}
