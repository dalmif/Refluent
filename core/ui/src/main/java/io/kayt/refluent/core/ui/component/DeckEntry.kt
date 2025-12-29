package io.kayt.refluent.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kayt.core.model.Card
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.misc.LocalTtsManager
import io.kayt.refluent.core.ui.misc.NoOpTTSManagerScope
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.DMSans
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir

@Composable
fun DeckEntry(
    card: Card,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
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
                                color = AppTheme.colors.textPrimary.copy(0.34f),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(" /${card.phonetic}/ ")
                        }
                    }
//                    appendInlineContent("audio", "audio")
                },
                inlineContent = mapOf(
                    "audio" to InlineTextContent(
                        Placeholder(
                            17.sp, 17.sp,
                            PlaceholderVerticalAlign.Center
                        ),
                        {
                            Icon(
                                painter = painterResource(R.drawable.icon_sound_wave),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(90.dp)
                                    .width(40.dp),
                            )
                        }
                    )
                ),
                style = TextStyle(
                    fontFamily = DMSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = AppTheme.colors.textPrimary
            )
            Text(
                text = card.back,
                fontSize = 17.sp,
                fontFamily = DMSansVazir,
                color = AppTheme.colors.textPrimary.copy(0.65f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        val ttsManager = LocalTtsManager.current
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 13.dp)
                .size(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(radius = 20.dp)
                ) {
                    ttsManager.speak(card.front)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_light_sound_wave),
                contentDescription = "Play audio",
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.textPrimary.copy(if (ttsManager.isAvailable) 0.4f else 0.3f)
            )
        }
    }
}

@Preview
@Composable
private fun DeckEntryPreview() {
    AppTheme {
        NoOpTTSManagerScope {
            DeckEntry(
                card = Card(0, "Hello", "Ciao", 12, true, "", "", "")
            )
        }
    }
}
