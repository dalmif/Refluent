package io.kayt.refluent.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kayt.core.model.Card
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.DMSans
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir

@Composable
fun DeckEntry(
    card: Card,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Column(
            modifier = Modifier.padding(
                horizontal = 17.dp,
                vertical = 14.dp
            )
        ) {
            Text(
                buildAnnotatedString {
                    append(card.front)
                    if (card.phonetic.isNotBlank()) {
                        withStyle(
                            SpanStyle(
                                color = AppTheme.colors.textNegativeSecondary,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(" /${card.phonetic}/")
                        }
                    }
//                                            appendInlineContent("audio", "audio")
                },
                inlineContent = mapOf(
                    "audio" to InlineTextContent(
                        Placeholder(
                            17.sp, 17.sp,
                            PlaceholderVerticalAlign.Center
                        ), {
                            Icon(
                                painter = painterResource(R.drawable.ic_light_sound_wave),
                                contentDescription = null,
                                modifier = Modifier.height(30.dp),
                                tint = Color(0xFFB2B2B2)
                            )
                        })
                ),
                style = TextStyle(
                    fontFamily = DMSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = Color.Black
            )
            Text(
                text = card.back,
                fontSize = 17.sp,
                fontFamily = DMSansVazir,
                color = Color(0xFF515151),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}