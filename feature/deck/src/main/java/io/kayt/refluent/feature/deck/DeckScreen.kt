package io.kayt.refluent.feature.deck

import androidx.compose.animation.Animatable
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import io.kayt.refluent.core.ui.component.DeckEntry
import io.kayt.refluent.core.ui.component.HeadlessTopmostAppBar
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.button.SecondaryButton
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.LifeSaver
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DeckScreen(
    onAddCardClick: () -> Unit,
    onStudyClick: () -> Unit,
    onEditCardClick: (cardId: Long) -> Unit,
    deckViewModel: DeckViewModel = hiltViewModel()
) {
    val state by deckViewModel.state.collectAsStateWithLifecycle()
    DeckScreen(
        state = state,
        onAddCardClick = onAddCardClick,
        onStudyClick = onStudyClick,
        onEditCardClick = onEditCardClick
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DeckScreen(
    state: DeckUiState,
    onAddCardClick: () -> Unit,
    onEditCardClick: (cardId: Long) -> Unit,
    onStudyClick: () -> Unit
) {
    with(LocalSharedTransitionScope.current) {
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
                            .sharedBounds(
                                rememberSharedContentState(key = "deck_background_${state.deck.id}"),
                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                            )
                            .fillMaxWidth()
                            .fillMaxHeight(0.45f)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(state.deck.colors.first),
                                        Color(state.deck.colors.second)
                                    )
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
                                            style = AppTheme.typography.headline1,
                                            modifier = Modifier.sharedElement(
                                                rememberSharedContentState(
                                                    key = "deck_title_${state.deck.id}"
                                                ),
                                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                                            )
                                        )
                                        Text(
                                            text = "${state.deck.totalCards} cards",
                                            style = AppTheme.typography.body2,
                                            modifier = Modifier.sharedElement(
                                                rememberSharedContentState(
                                                    key = "deck_total_cards_${state.deck.id}"
                                                ),
                                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                                            )
                                        )
                                    }
                                }
                                Spacer(Modifier.width(32.dp))
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    val pulseColor: Color = Color.Red
                                    val baseScale = 1f
                                    val pulseScale = 1.12f
                                    val baseColor = AppTheme.colors.textPrimary
                                    val scale = remember { Animatable(1f) }
                                    val color = remember { Animatable(baseColor) }
                                    val lastDeckSize =
                                        remember { mutableIntStateOf(state.deck.dueCards) }
                                    LaunchedEffect(state.deck.dueCards) {
                                        if (lastDeckSize.intValue != state.deck.dueCards) {
                                            lastDeckSize.intValue = state.deck.dueCards
                                            coroutineScope {
                                                launch {
                                                    scale.animateTo(
                                                        pulseScale,
                                                        tween(140, easing = FastOutSlowInEasing)
                                                    )
                                                }
                                                launch { color.animateTo(pulseColor, tween(140)) }
                                            }
                                            coroutineScope {
                                                launch {
                                                    scale.animateTo(
                                                        baseScale,
                                                        tween(180, easing = FastOutSlowInEasing)
                                                    )
                                                }
                                                launch { color.animateTo(baseColor, tween(180)) }
                                            }
                                        }
                                    }
                                    Text(
                                        text = state.deck.dueCards.toString(),
                                        style = AppTheme.typography.subhead,
                                        color = color.value,
                                        modifier = Modifier
                                            .sharedElement(
                                                rememberSharedContentState(
                                                    key = "deck_due_cards_${state.deck.id}"
                                                ),
                                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                                            )
                                            .graphicsLayer {
                                                scaleX = scale.value
                                                scaleY = scale.value
                                            }
                                    )
                                    Text(
                                        text = "due for reviews",
                                        style = AppTheme.typography.body1,
                                        modifier = Modifier.sharedElement(
                                            rememberSharedContentState(
                                                key = "deck_due_text_${state.deck.id}"
                                            ),
                                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                                        )
                                    )
                                }
                            }

                            HeadlessTopmostAppBar(
                                topmostAppBarState,
                                modifier = Modifier.renderInSharedTransitionScopeOverlay()
                                    .applyIf(true) {
                                        LocalNavAnimatedVisibilityScope.current.run {
                                            this@applyIf.animateEnterExit(
                                                enter = slideInVertically { it * 3 } + fadeIn(
                                                    tween(
                                                        700,
                                                        delayMillis = 100
                                                    )
                                                ) + scaleIn(
                                                    tween(300, delayMillis = 100),
                                                    initialScale = 0.4f
                                                ),
                                                exit = slideOutVertically { it * 2 } + fadeOut()
                                            )
                                        }
                                    },
                                minHeight = 25.dp
                            ) { _, collapseFraction ->
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
                                    PrimaryButton(
                                        { onStudyClick() },
                                        modifier = Modifier.weight(1f, true)
                                    ) {
                                        Text("Start study")
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .renderInSharedTransitionScopeOverlay()
                                .applyIf(true) {
                                    LocalNavAnimatedVisibilityScope.current.run {
                                        this@applyIf.animateEnterExit(
                                            enter = slideInVertically { it / 2 } + fadeIn(),
                                            exit = slideOutVertically { it / 2 } + fadeOut()
                                        )
                                    }
                                }
                                .fillMaxWidth()
                                .fillMaxHeight()
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
                                .background(AppTheme.colors.background)) {
                            if (state.cards.isNotEmpty()) {
                                LazyColumn(
                                    state = lazyColumnState,
                                    modifier = Modifier
                                        .nestedScroll(topmostAppBarState.nestedScrollConnection)
                                ) {
                                    items(state.cards.size) {
                                        val card = state.cards[it]
                                        DeckEntry(
                                            card = card,
                                            modifier = Modifier.clickable {
                                                onEditCardClick(state.cards[it].id)
                                            }
                                                .applyIf(it == 0) {
                                                    padding(top = 10.dp)
                                                }.applyIf(it == state.cards.lastIndex) {
                                                    padding(bottom = 10.dp)
                                                }
                                        )
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            color = Color(0xFFEFEFEF)
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(
                                                PaddingValues(bottom = innerPadding.calculateBottomPadding())
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(Modifier.weight(1f))
                                        Box(modifier = Modifier.offset(x = 30.dp, y = 0.dp)) {
                                            Spacer(
                                                Modifier
                                                    .offset(x = -(193 / 5).dp, y = 0.dp)
                                                    .size(193.dp)
                                                    .background(Color(0xFFFFF9D4), CircleShape)
                                            )
                                            Image(
                                                painter = painterResource(R.drawable.first_ever_brainy),
                                                contentDescription = null,
                                                modifier = Modifier.size(137.dp, 185.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.weight(1.4f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No Cards Found",
                                                fontFamily = LifeSaver,
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 55.sp,
                                                modifier = Modifier.padding(top = 30.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(50.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}