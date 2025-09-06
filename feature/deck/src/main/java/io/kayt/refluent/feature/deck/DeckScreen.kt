package io.kayt.refluent.feature.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.button.SecondaryButton
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.DMSans

@Composable
fun DeckScreen(
    deckViewModel: DeckViewModel = hiltViewModel()
) {
    DeckScreen()
}

@Composable
private fun DeckScreen() {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.background(Color(0xFFBBE2A8))) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 21.dp, end = 16.dp)
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(top = 38.dp, bottom = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        Column {
                            Text(
                                "English with Kiana".uppercase(),
                                style = AppTheme.typography.headline1
                            )
                            Text(
                                text = "2510 cards",
                                style = AppTheme.typography.body2
                            )
                        }
                    }
                    Spacer(Modifier.width(32.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "324",
                            style = AppTheme.typography.subhead
                        )
                        Text(
                            text = "due for reviews",
                            style = AppTheme.typography.body1
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    SecondaryButton({

                    }) {
                        Text("Add card")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    PrimaryButton({

                    }, modifier = Modifier.weight(1f, true)) {
                        Text("Start study")
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .background(AppTheme.colors.background),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
            ) {
                item {
                    Text(
                        text = "Cards in deck",
                        style = AppTheme.typography.headline2,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(vertical = 22.dp)
                    )
                }
                items(100) {
                    Column {
                        Column(modifier = Modifier.padding(horizontal = 17.dp, vertical = 14.dp)) {
                            Text(
                                buildAnnotatedString {
                                    append("Ceremony")
                                    withStyle(SpanStyle(color = AppTheme.colors.textNegativeSecondary)) {
                                        append("/ˈsɛrəˌmoʊni/")
                                    }
                                    appendInlineContent("audio", "audio")
                                },
                                inlineContent = mapOf(
                                    "audio" to InlineTextContent(
                                        Placeholder(
                                            10.sp, 10.sp,
                                            PlaceholderVerticalAlign.Center
                                        ), {
                                            Text("Audio")
                                        })
                                ),
                                style = TextStyle(
                                    fontFamily = DMSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                "مراسم",
                                style = TextStyle(
                                    fontFamily = DMSans,
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}