package io.kayt.refluent.core.ui.theme.typography

import android.annotation.SuppressLint
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val appTypography = AppTypography(
    greeting1 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        letterSpacing = 0.sp,
        lineHeight = 45.sp
    ),
    large = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 31.sp,
        letterSpacing = 0.sp,
        lineHeight = 30.sp
    ),
    headline1 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 31.sp,
        letterSpacing = 0.sp,
        lineHeight = 30.sp
    ),
    headline2 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        letterSpacing = 0.sp,
        lineHeight = 30.sp,
    ),
    headline3 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 23.sp,
    ),
    headline4 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    body1 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    ),
    body2 = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    ),
    tag = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    description = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    cardText = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
        lineHeight = 36.sp
    ),
    cta = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
    ),
    textFieldTitle = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    subhead = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 48.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    ),
    button = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    )
)

@SuppressLint("ComposeCompositionLocalUsage")
internal val LocalAppTypography = staticCompositionLocalOf { appTypography }
