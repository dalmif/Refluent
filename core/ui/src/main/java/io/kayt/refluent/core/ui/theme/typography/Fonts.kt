package io.kayt.refluent.core.ui.theme.typography

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.kayt.refluent.core.ui.R

@OptIn(ExperimentalTextApi::class)
val DMSans = FontFamily(
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W100,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(100)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W200,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(200)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W300,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(300)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W400,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W500,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W600,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W700,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W800,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(800)
        )
    ),
    Font(
        resId = R.font.dmsans,
        weight = FontWeight.W900,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(900)
        )
    )
)