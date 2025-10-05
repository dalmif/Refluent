package io.kayt.refluent.core.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.kayt.refluent.core.ui.theme.colors.AppColors
import io.kayt.refluent.core.ui.theme.colors.AppThemeLightColors
import io.kayt.refluent.core.ui.theme.colors.LocalAppColors
import io.kayt.refluent.core.ui.theme.colors.MaterialThemeLightColors
import io.kayt.refluent.core.ui.theme.typography.AppTypography
import io.kayt.refluent.core.ui.theme.typography.LocalAppTypography
import io.kayt.refluent.core.ui.theme.typography.appTypography

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    val materialColors = MaterialThemeLightColors
    val appColors = AppThemeLightColors

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppTypography provides appTypography,
        LocalTonalElevationEnabled provides false
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = {
                Surface(content = content)
            }
        )
    }
}
