package io.kayt.refluent.core.ui.component.button

import android.R.attr.onClick
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kayt.refluent.core.ui.theme.AppTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier.height(61.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppTheme.colors.backgroundDark
        ),
        onClick = onClick,
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides AppTheme.typography.button,
                LocalContentColor provides AppTheme.colors.onBackgroundDark
            ) {
                content()
            }
        }
    )
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    PrimaryButton(
        onClick = {},
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides AppTheme.typography.button,
                LocalContentColor provides AppTheme.colors.onBackgroundDark
            ) {
                Text("Start Review")
            }
        }
    )
}