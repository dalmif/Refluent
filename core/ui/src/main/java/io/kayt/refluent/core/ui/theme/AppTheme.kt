package io.kayt.refluent.core.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.kayt.refluent.core.ui.theme.colors.AppColors
import io.kayt.refluent.core.ui.theme.colors.AppThemeDarkColors
import io.kayt.refluent.core.ui.theme.colors.AppThemeLightColors
import io.kayt.refluent.core.ui.theme.colors.LocalAppColors
import io.kayt.refluent.core.ui.theme.colors.LocalDarkUi
import io.kayt.refluent.core.ui.theme.colors.MaterialThemeDarkColors
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

    val isDark: Boolean
        @Composable
        get() = LocalDarkUi.current
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun AppTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalAppColors provides if (!isDark) AppThemeLightColors else AppThemeDarkColors,
        LocalAppTypography provides appTypography,
        LocalTonalElevationEnabled provides false,
        LocalDarkUi provides isDark
    ) {
        MaterialTheme(
            colorScheme = if (!isDark) MaterialThemeLightColors else MaterialThemeDarkColors,
            content = {
                Surface(content = content)
            }
        )
    }
}
