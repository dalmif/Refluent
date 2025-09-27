package io.kayt.refluent.feature.deck

import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.HeadlessTopmostAppBar
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.button.SecondaryButton
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.Charis
import io.kayt.refluent.core.ui.theme.typography.DMSans
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir

@Composable
fun DeckScreen(
    onAddCardClick: () -> Unit,
    onStudyClick: () -> Unit,
    deckViewModel: DeckViewModel = hiltViewModel()
) {
    val state by deckViewModel.state.collectAsStateWithLifecycle()
    DeckScreen(
        state = state,
        onAddCardClick = onAddCardClick,
        onStudyClick = onStudyClick,
    )
}

@Composable
private fun DeckScreen(
    state: DeckUiState,
    onAddCardClick: () -> Unit,
    onStudyClick: () -> Unit,
) {
    if (state is DeckUiState.Success) {
        val lazyColumnState = rememberLazyListState()
        val topmostAppBarState =
            rememberTopmostAppBarState(canScroll = { lazyColumnState.canScrollForward || lazyColumnState.canScrollBackward })
        Scaffold { innerPadding ->
            Box(
                Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.45f)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFDDCAA), Color(0xFFECDBDA))
                            )
                        )
                )
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 21.dp, end = 16.dp)
                            .padding(top = innerPadding.calculateTopPadding())
                            .padding(top = 38.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.weight(1f)) {
                                Column {
                                    Text(
                                        state.deck.name.uppercase(),
                                        style = AppTheme.typography.headline1
                                    )
                                    Text(
                                        text = "${state.deck.totalCards} cards",
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
                                    text = state.deck.dueCards.toString(),
                                    style = AppTheme.typography.subhead
                                )
                                Text(
                                    text = "due for reviews",
                                    style = AppTheme.typography.body1
                                )
                            }
                        }
                        HeadlessTopmostAppBar(topmostAppBarState, minHeight = 25.dp) { _, collapseFraction ->
                            val scale = lerp(1f, 0.9f, EaseOutQuint.transform(collapseFraction))
                            val offset = lerp(0.dp, 40.dp, collapseFraction)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(scale)
                                    .offset {
                                        IntOffset(x = 0, y = offset.roundToPx())
                                    }
                                    .padding(top = 25.dp, bottom = 20.dp)
                            ) {
                                SecondaryButton({ onAddCardClick() }) {
                                    Text("Add card")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                PrimaryButton({ onStudyClick() }, modifier = Modifier.weight(1f, true)) {
                                    Text("Start study")
                                }
                            }
                        }
                    }
                    LazyColumn(
                        state = lazyColumnState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .dropShadow(
                                shape = RoundedCornerShape(25.dp),
                                shadow = Shadow(
                                    radius = 14.dp,
                                    color = Color.Black.copy(alpha = 0.13f),
                                    offset = DpOffset(0.dp, 4.dp)
                                )
                            )
                            .clip(RoundedCornerShape(25.dp))
                            .background(
                                AppTheme.colors.background
                            )
                            .nestedScroll(topmostAppBarState.nestedScrollConnection)
                    ) {
                        items(state.cards.size) {
                            val card = state.cards[it]
                            Column(Modifier.clickable {}) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 17.dp,
                                        vertical = 14.dp
                                    ).applyIf(it == 0) {
                                        padding(top = 10.dp)
                                    }.applyIf(it == state.cards.lastIndex) {
                                        padding(bottom = 10.dp)
                                    }
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
                                if (it != state.cards.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        color = Color(0xFFEFEFEF)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}