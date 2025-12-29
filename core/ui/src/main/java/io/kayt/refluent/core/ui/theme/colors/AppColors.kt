package io.kayt.refluent.core.ui.theme.colors

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val agree : Color,
    val disagree : Color,
    val rotateButton : Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHighlight: Color,
    val textNegativePrimary: Color,
    val textNegativeSecondary: Color,
    val textCTAPrimary: Color,
    val textCTASecondary: Color,
    val background: Color,
    val yellowOnBackground: Color,
    val surface: Color,
    val cardBackground: Color,
    val searchBoxBackground: Color,
    val searchBoxContent: Color,
    val searchBoxBorder: Color,
    val backgroundGrey: Color,
    val backgroundGrey2: Color,
    val backgroundNegative: Color,
    val backgroundPurple: Color,
    val backgroundDark: Color,
    val onBackgroundDark: Color,
    val ctaPrimary: Color,
    val ctaSecondary: Color,
    val ctaIconPrimary: Color,
    val ctaIconSecondary: Color,
    val ctaIconNegative: Color,
    val ctaIconNegative2: Color,
    val ctaLiveShow: Color,
    val navIconPrimary: Color,
    val navIconNegative: Color,
    val illustrationPrimary: Color,
    val illustrationNegative: Color,
    val separator: Color,
    val validation: Color,
    val error: Color,
    val onError: Color,
    val closeButtonColor: Color,
    val divider: Color
)

@SuppressLint("ComposeCompositionLocalUsage")
internal val LocalAppColors = staticCompositionLocalOf {
    AppThemeLightColors
}


@SuppressLint("ComposeCompositionLocalUsage")
internal val LocalDarkUi = staticCompositionLocalOf {
    false
}
