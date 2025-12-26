package io.kayt.refluent.feature.deck

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kayt.core.model.ReviewMode
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.DeckEntry
import io.kayt.refluent.core.ui.component.HeadlessTopmostAppBar
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.core.ui.component.MeshGradient
import io.kayt.refluent.core.ui.component.background
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.button.SecondaryButton
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.LifeSaver
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
        onEditCardClick = onEditCardClick,
        onReviewModeChange = deckViewModel::updateReviewMode
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DeckScreen(
    state: DeckUiState,
    onAddCardClick: () -> Unit,
    onEditCardClick: (cardId: Long) -> Unit,
    onStudyClick: () -> Unit,
    onReviewModeChange: (ReviewMode) -> Unit
) {
    with(LocalSharedTransitionScope.current) {
        if (state is DeckUiState.Success) {
            val lazyColumnState = rememberLazyListState()
            val topmostAppBarState = rememberTopmostAppBarState(canScroll = { true })
            Scaffold(
                containerColor = AppTheme.colors.background
            ) { innerPadding ->
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
                            .applyIf(!AppTheme.isDark) {
                                background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(state.deck.colors.first),
                                            Color(state.deck.colors.second)
                                        )
                                    )
                                )
                            }.applyIf(AppTheme.isDark) {
                                background(AppTheme.colors.cardBackground)
                                    .scale(1.1f)
                                    .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                                    .background(
                                        MeshGradient(
                                            width = 3,
                                            height = 2,
                                            points = {
                                                point(0.8f, 0f, Color(0x4AFFE292))
                                                point(
                                                    0.2f,
                                                    0f,
                                                    Color(state.deck.colors.first).copy(0.4f)
                                                )
                                            }
                                        ))
                            }
                    )
                    Column {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 21.dp, end = 16.dp)
                                .padding(top = innerPadding.calculateTopPadding())
                                .padding(top = 38.dp)
                        ) {
                            var timePointingIcon by remember { mutableStateOf(false) }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.weight(1f)) {
                                    Column {
                                        Text(
                                            state.deck.name.uppercase(),
                                            style = AppTheme.typography.headline1,
                                            maxLines = 4,
                                            color = if (AppTheme.isDark) {
                                                Color(state.deck.colors.first)
                                            } else {
                                                AppTheme.colors.textPrimary
                                            },
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
                                            color = AppTheme.colors.textPrimary,
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
                                    Box(
                                        Modifier
                                            .sharedElement(
                                                rememberSharedContentState(key = "deck_due_cards_${state.deck.id}"),
                                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                                            ),
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        if (state.deck.totalCards == 0 || state.deck.dueCards > 0) {
                                            Text(
                                                text = state.deck.dueCards.toString(),
                                                style = AppTheme.typography.subhead,
                                                color = color.value,
                                                modifier = Modifier
                                                    .graphicsLayer {
                                                        scaleX = scale.value
                                                        scaleY = scale.value
                                                    }
                                            )
                                        } else {
                                            AnimatedContent(
                                                timePointingIcon,
                                                transitionSpec = {
                                                    slideInHorizontally { it } togetherWith slideOutHorizontally { it }
                                                }
                                            ) {
                                                Icon(
                                                    if (!it) {
                                                        painterResource(R.drawable.brainstorming)
                                                    } else {
                                                        painterResource(R.drawable.no_card_yet)
                                                    },
                                                    null,
                                                    modifier = Modifier.size(80.dp),
                                                    tint = AppTheme.colors.textPrimary
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = if (state.deck.totalCards == 0 || state.deck.dueCards > 0) {
                                            "due for reviews"
                                        } else {
                                            "All caught up!"
                                        },
                                        style = AppTheme.typography.body1,
                                        color = AppTheme.colors.textPrimary,
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
                                        Text("Add Card")
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val coroutine = rememberCoroutineScope()
                                    PrimaryButton(
                                        {
                                            if (state.deck.dueCards > 0) {
                                                onStudyClick()
                                            } else {
                                                coroutine.launch {
                                                    timePointingIcon = true
                                                    delay(2000)
                                                    timePointingIcon = false
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f, true)
                                    ) {
                                        Text("Start Review")
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
                                .background(
                                    AppTheme.colors.surface
                                )
                        ) {
                            if (state.cards.isNotEmpty()) {
                                var isSettingVisible by remember { mutableStateOf(false) }
                                BackHandler(isSettingVisible) {
                                    isSettingVisible = false
                                }
                                AnimatedContent(
                                    isSettingVisible,
                                    transitionSpec = {
                                        if (!initialState) {
                                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                                        } else {
                                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                                        }
                                    }
                                ) { settingVisible ->
                                    if (!settingVisible) {
                                        LazyColumn(
                                            state = lazyColumnState,
                                            verticalArrangement = TopWithFooter,
                                            modifier = Modifier
                                                .fillMaxHeight()
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
                                                if (it != state.cards.lastIndex) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(horizontal = 10.dp),
                                                        color = AppTheme.colors.divider
                                                    )
                                                }
                                            }
                                            item {
                                                ReviewModeItem(
                                                    currentReviewMode = state.deck.reviewMode,
                                                    onClick = { isSettingVisible = true }
                                                )
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .nestedScroll(topmostAppBarState.nestedScrollConnection)
                                        ) {
                                            SettingScreen(
                                                onReviewModeOptionClick = {
                                                    onReviewModeChange(it)
                                                },
                                                selected = state.settings.reviewMode,
                                                onBackClick = { isSettingVisible = false }
                                            )
                                        }
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
                                        Box(
                                            modifier = Modifier
                                                .offset(x = 30.dp, y = 0.dp)
                                                .applyIf(AppTheme.isDark) { alpha(0f) }
                                        ) {
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
                                                color = AppTheme.colors.textPrimary,
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

@Composable
private fun ReviewModeItem(
    currentReviewMode: ReviewMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.yellowOnBackground)
            .clickable(onClick = onClick)
            .padding(top = 23.dp, bottom = 20.dp)
            .padding(start = 19.dp, end = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = "Change your review mode",
                color = AppTheme.colors.textPrimary,
                style = AppTheme.typography.body1.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                ),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = currentReviewMode.asTitle(),
                color = Color(0xFFA9A066),
                style = AppTheme.typography.body1.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF9F8D1A))
                .clickable(onClick = onClick)
                .size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun SettingScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    selected: ReviewMode,
    onReviewModeOptionClick: (ReviewMode) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_backward),
                    contentDescription = null,
                    tint =  AppTheme.colors.textPrimary
                )
            }
            Text(
                "Review Mode",
                color = AppTheme.colors.textPrimary,
                style = AppTheme.typography.body1.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            val options = ReviewMode.entries
            options.mapIndexed { index, item ->
                ReviewModeOption(
                    title = item.asTitle(),
                    description = item.asDescription(),
                    selected = selected == item,
                    onClick = {
                        onReviewModeOptionClick(item)
                    }
                )
                if (options.lastIndex != index) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 30.dp),
                        color = AppTheme.colors.divider
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewModeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = AppTheme.colors.textPrimary.copy(alpha = 0.8f)
            )
        )
        Column {
            Text(
                title,
                style = AppTheme.typography.body1.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = AppTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                description,
                style = AppTheme.typography.body1.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = AppTheme.colors.textPrimary.copy(0.5f)
            )
        }
    }
}

@Composable
private fun ReviewMode.asTitle(): String {
    return when (this) {
        ReviewMode.FrontFirst -> "Front-First Mode"
        ReviewMode.BackFirst -> "Back-First Mode"
        ReviewMode.ShuffleSides -> "Shuffle Sides"
        ReviewMode.DualSided -> "Dual-Sided"
    }
}

@Composable
private fun ReviewMode.asDescription(): String {
    return when (this) {
        ReviewMode.FrontFirst -> "Review all cards starting from the front side for a standard, forward-direction practice"
        ReviewMode.BackFirst -> "Review all cards with the back shown as the front. A complete flipped-session for testing deeper recall."
        ReviewMode.ShuffleSides -> "Each card is randomly shown from either its front or back, creating a varied, unpredictable review session."
        ReviewMode.DualSided -> "Both sides of every card are included as separate prompts. You’ll see every front and every back, all shuffled together."
    }
}


object TopWithFooter : Arrangement.Vertical {
    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        outPositions: IntArray
    ) {
        var y = 0
        sizes.forEachIndexed { index, size ->
            outPositions[index] = y
            y += size
        }
        if (y < totalSize) {
            val lastIndex = outPositions.lastIndex
            outPositions[lastIndex] = totalSize - sizes.last()
        }
    }

}