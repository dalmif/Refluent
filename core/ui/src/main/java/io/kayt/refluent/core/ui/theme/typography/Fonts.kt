package io.kayt.refluent.core.ui.theme.typography

import android.app.Application
import android.os.Build
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.kayt.refluent.core.ui.R
import android.graphics.Typeface as AndroidTypeface
import android.graphics.fonts.Font as AndroidFont


var DMSansVazir: FontFamily? = null

// This should be called from Application class
@OptIn(ExperimentalTextApi::class)
fun Application.setMixedFont() {
    DMSansVazir != null && return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val font = AndroidFont.Builder(resources, R.font.dmsans).build()
        val mainFont = android.graphics.fonts.FontFamily.Builder(font).build()
        val regularVazir = AndroidFont.Builder(resources, R.font.vazirmatn).build()
        val fallbackFamily = android.graphics.fonts.FontFamily.Builder(regularVazir).build()
        val mixedFont = AndroidTypeface.CustomFallbackBuilder(mainFont)
            .addCustomFallback(fallbackFamily)
            .build()
        DMSansVazir = FontFamily(AndroidTypefaceProxy.create(mixedFont))
    } else {
        DMSansVazir = FontFamily(
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W100,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(100)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W200,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(200)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W300,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(300)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W400,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(400)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W500,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(500)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W600,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(600)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W700,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(700)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W800,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(800)
                )
            ),
            Font(
                resId = R.font.vazirmatn,
                weight = FontWeight.W900,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(900)
                )
            )
        )
    }
}