package io.kayt.refluent.feature.deck.flashcard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.SwipeableCardsFactors
import com.spartapps.swipeablecards.ui.SwipeableCardsProperties
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items
import io.kayt.core.model.Card
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.DashedDivider
import io.kayt.refluent.core.ui.component.MeshGradient
import io.kayt.refluent.core.ui.component.animateOffsetOnBorder
import io.kayt.refluent.core.ui.component.button.SecondaryBigButton
import io.kayt.refluent.core.ui.component.fadingEdges
import io.kayt.refluent.core.ui.misc.LocalTtsManager
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random

@Composable
fun FlashcardScreen(
    onBackClick: () -> Unit,
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FlashcardScreen(
        state = state,
        onSwipeRight = viewModel::markCardAsGood,
        onSwipeLeft = viewModel::markCardAsBad,
        onBackClick = onBackClick
    )
}

@Composable
private fun FlashcardScreen(
    state: FlashcardUiState,
    onBackClick: () -> Unit,
    onSwipeRight: (Card) -> Unit,
    onSwipeLeft: (Card) -> Unit,
) {
    val meshState by animateOffsetOnBorder(20_000)
    MeshGradient(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        mesh = MeshGradient(
            width = 3,
            height = 5,
            points = {
                point(meshState.x, meshState.y, Color(0x4AFFE292))
            }
        )
    ) {
        var transparentBackground by remember { mutableStateOf(false) }
        val backgroundColor = remember(state) {
            if (state is FlashcardUiState.Success) {
                val colors = state.deck.colors
                listOf(Color(colors.first), Color(colors.second))
            } else {
                listOf(Color(0xFFFDDCAA), Color(0xFFECDBDA))
            }
        }
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .applyIf(!transparentBackground) {
                        background(
                            Brush.horizontalGradient(
                                backgroundColor
                            )
                        )
                            .background(Color.Black.copy(alpha = 0.3f))
                    }
            ) {
                when (state) {
                    is FlashcardUiState.Loading -> {
                        // Loading state
                    }

                    is FlashcardUiState.Success -> {
                        val flipState = remember { mutableStateOf(0 to false) }
                        val cardSize = state.cards.size
                        val swipeableState = rememberSwipeableCardsState(
                            itemCount = { cardSize }
                        )
                        val cardVisible =
                            remember { derivedStateOf { swipeableState.currentCardIndex != state.cards.size } }
                        LaunchedEffect(cardVisible.value) {
                            transparentBackground = !cardVisible.value
                        }
                        AnimatedContent(cardVisible.value, modifier = Modifier) {
                            if (it) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    LazySwipeableCards(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 40.dp),
                                        state = swipeableState,
                                        properties = SwipeableCardsProperties(
                                            padding = 0.dp,
                                            stackedCardsOffset = 0.dp
                                        ),
                                        factors = SwipeableCardsFactors(
                                            cardOffsetCalculation = { index, _, _ -> Offset.Zero }
                                        ),
                                        onSwipe = { card, direction ->
                                            when (direction) {
                                                SwipeableCardDirection.Right -> {
                                                    onSwipeRight(card)
                                                }

                                                SwipeableCardDirection.Left -> {
                                                    onSwipeLeft(card)
                                                }
                                            }
                                        }
                                    ) {
                                        items(state.cards) { card, index, offset ->
                                            Box(
                                                modifier = Modifier.graphicsLayer {
                                                    rotationZ =
                                                        if (swipeableState.currentCardIndex == index) 0f else (-1f).pow(
                                                            index % 2
                                                        ) * 1f
                                                    scaleY =
                                                        if (index == swipeableState.currentCardIndex) 1f else 1.04f
                                                }
                                            ) {
                                                SwipeableCard(
                                                    card = card,
                                                    isOnTop = swipeableState.currentCardIndex >= index,
                                                    isVisible = swipeableState.currentCardIndex + 1 >= index,
                                                    isFront = flipState.value.second.not()
                                                        .takeIf { flipState.value.first == index }
                                                        ?: true,
                                                    isVirtualBackCard = card.isVirtualBackCard,
                                                    onFlipRequested = {
                                                        flipState.value =
                                                            if (flipState.value.first == index) {
                                                                index to !flipState.value.second
                                                            } else {
                                                                index to true
                                                            }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Column(Modifier) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            ActionButton(
                                                icon = painterResource(R.drawable.ic_wrong),
                                                backgroundColor = Color(0x99FFFFFF),
                                                borderColor = Color(0xFFB22525),
                                                iconTint = Color(0xFFB22525),
                                                iconSize = 38.dp,
                                                onClick = {
                                                    if (state.cards.size > swipeableState.currentCardIndex) {
                                                        onSwipeLeft(state.cards[swipeableState.currentCardIndex])
                                                    }
                                                    swipeableState.swipe(SwipeableCardDirection.Left)
                                                }
                                            )
                                            Spacer(Modifier.width(30.dp))
                                            ActionButton(
                                                icon = painterResource(R.drawable.ic_exchange),
                                                backgroundColor = Color(0xFF3A50AF),
                                                borderColor = Color(0xFF3A50AF),
                                                iconTint = Color.White,
                                                size = 97.dp,
                                                iconSize = 37.dp,
                                                shadow = true,
                                                onClick = {
                                                    flipState.value =
                                                        if (flipState.value.first == swipeableState.currentCardIndex) {
                                                            swipeableState.currentCardIndex to !flipState.value.second
                                                        } else {
                                                            swipeableState.currentCardIndex to true
                                                        }
                                                }
                                            )
                                            Spacer(Modifier.width(30.dp))
                                            ActionButton(
                                                icon = painterResource(R.drawable.ic_vector),
                                                backgroundColor = Color(0x99FFFFFF),
                                                borderColor = Color(0xFF1B8F1D),
                                                iconTint = Color(0xFF1B8F1D),
                                                iconSize = 33.dp,
                                                onClick = {
                                                    if (state.cards.size > swipeableState.currentCardIndex) {
                                                        onSwipeRight(state.cards[swipeableState.currentCardIndex])
                                                    }
                                                    swipeableState.swipe(SwipeableCardDirection.Right)
                                                }
                                            )
                                        }
                                        Spacer(Modifier.height(36.dp))
                                        SecondaryBigButton(
                                            onBackClick,
                                            background = Color(0xFFDED7D5),
                                            height = 63.dp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp)
                                                .padding(horizontal = 30.dp)
                                        ) {
                                            Text("End Review")
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    var konfettiPosition by remember {
                                        mutableStateOf<Pair<Offset, Offset>?>(null)
                                    }
                                    val party = remember(konfettiPosition) {
                                        val konfettiPosition = konfettiPosition
                                        if (konfettiPosition == null) return@remember null
                                        val party = Party(
                                            speed = 10f,
                                            maxSpeed = 60f,
                                            damping = 0.9f,
                                            angle = Angle.RIGHT - 74,
                                            spread = Spread.WIDE,
                                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                            emitter = Emitter(
                                                duration = 2500,
                                                TimeUnit.MILLISECONDS
                                            ).perSecond(90),
                                            position = Position.Absolute(
                                                konfettiPosition.first.x,
                                                konfettiPosition.first.y
                                            )
                                        )
                                        listOf(
                                            party,
                                            party.copy(
                                                angle = Angle.RIGHT - 90 - 18,
                                                position = Position.Absolute(
                                                    konfettiPosition.second.x,
                                                    konfettiPosition.second.y
                                                )
                                            ),
                                        )
                                    }
                                    if (party != null) {
                                        KonfettiView(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                                .align(Alignment.Center),
                                            parties = party,
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .padding(bottom = innerPadding.calculateBottomPadding())
                                            .fillMaxHeight(0.7f)
                                            .align(Alignment.BottomCenter),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val density = LocalDensity.current
                                        Image(
                                            painter = painterResource(R.drawable.congrat_brainy),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .width(218.dp)
                                                .onGloballyPositioned({ layoutCoordinates ->
                                                    val position =
                                                        layoutCoordinates.positionInWindow()
                                                    val size = layoutCoordinates.size
                                                    konfettiPosition = Offset(
                                                        x = position.x + density.run { 12.dp.roundToPx() },
                                                        y = position.y + density.run { 12.dp.roundToPx() }
                                                    ) to Offset(
                                                        x = position.x + size.width - density.run { 14.dp.roundToPx() },
                                                        y = position.y + density.run { 12.dp.roundToPx() }
                                                    )
                                                })
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Text(
                                            "Well done!",
                                            style = AppTheme.typography.headline1.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Text(
                                            "You’ve reviewed every card",
                                            style = AppTheme.typography.headline3.copy(fontWeight = FontWeight.Normal),
                                            color = AppTheme.colors.textPrimary,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 24.dp)
                                                .width(270.dp)
                                                .height(IntrinsicSize.Max)
                                        ) {
                                            Spacer(
                                                Modifier
                                                    .clip(CircleShape)
                                                    .fillMaxHeight()
                                                    .width(4.dp)
                                                    .background(Color(0xFFF5D923))
                                            )
                                            Spacer(Modifier.width(15.dp))
                                            val randomPhrase = remember {
                                                motivationalPhrases[Random.nextInt(
                                                    motivationalPhrases.size
                                                )]
                                            }
                                            Text(
                                                randomPhrase,
                                                style = AppTheme.typography.body1,
                                                color = AppTheme.colors.textPrimary.copy(alpha = 0.8f)
                                            )
                                        }

                                        Box(
                                            Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "keep it up! \uD83D\uDCAA",
                                                style = AppTheme.typography.headline2.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = AppTheme.colors.textPrimary
                                            )
                                        }

                                        SecondaryBigButton(
                                            onBackClick,
                                            height = 63.dp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp)
                                                .padding(horizontal = 30.dp)
                                        ) {
                                            Text("End Review")
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
}

@Composable
private fun SwipeableCard(
    card: Card,
    isOnTop: Boolean,
    isFront: Boolean,
    onFlipRequested: () -> Unit,
    isVirtualBackCard: Boolean,
    isVisible: Boolean,
) {
    val front = if (isVirtualBackCard) -180f else 0f
    val back = if (isVirtualBackCard) 0f else -180f
    val targetAngle = if (isFront) front else back
    val rotation = animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ),
        label = "Flipping card animation"
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 20f * density
                }
                .fillMaxWidth(0.9f)
                .padding(vertical = 30.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .clickable(
                    onClick = { onFlipRequested() },
                    indication = null,
                    interactionSource = null
                )
                .background(Color.White.copy(if (isOnTop) 1f else 0.3f))
                .padding(32.dp)
        ) {
            val isAngleLessThanUpright by remember { derivedStateOf { rotation.value >= -90f } }
            if (isAngleLessThanUpright) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isVisible) 1f else 0f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = card.front,
                            style = AppTheme.typography.cardText,
                            color = Color.Black.copy(alpha = if (isVisible) 1f else 0f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phonetic with audio icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.alpha(alpha = if (isVisible) 1f else 0f)
                        ) {
                            Text(
                                text = "/${card.phonetic}/".takeIf { card.phonetic.isNotBlank() }
                                    ?: "",
                                style = AppTheme.typography.body1,
                                color = Color(0xFFA4A4A4)
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            val ttfManager = LocalTtsManager.current
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(radius = 20.dp)
                                    ) {
                                        ttfManager.speak(card.front)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_light_sound_wave),
                                    contentDescription = "Play audio",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (ttfManager.isAvailable) Color(0xFFB2B2B2) else Color(
                                        0xFFCCCCCC
                                    )
                                )
                            }
                        }
                    }

                    if (card.isVirtualBackCard) {
                        commentSectionInCard(card)
                    }
                }
            } else {
                Column(
                    Modifier.graphicsLayer {
                        rotationY = 180f
                    }.alpha(if (isVisible) 1f else 0f),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val scrollState = rememberScrollState()

                        Text(
                            text = card.back,
                            style = AppTheme.typography.cardText.copy(fontFamily = DMSansVazir),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .verticalScroll(scrollState)
                                .fadingEdges(scrollState),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (!card.isVirtualBackCard) {
                        commentSectionInCard(card)
                    }
                }
            }
        }
    }
}


@Composable
private fun ColumnScope.commentSectionInCard(
    card: Card,
    modifier: Modifier = Modifier
) {
    val plainText =
        HtmlCompat.fromHtml(card.comment, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
            .trim()
    if (plainText.isNotBlank()) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(top = 20.dp)
        ) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_comment),
                    contentDescription = null,
                    tint = Color(0xFF515151),
                    modifier = Modifier
                        .offset(y = 2.dp)
                        .size(17.dp)
                )
                Text(
                    text = "Comment",
                    style = AppTheme.typography.headline4,
                    color = Color(0xFF515151),
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
            DashedDivider(
                color = Color(0xFFDFDFDF),
                thickness = 1.dp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
            val scrollState = rememberScrollState()
            Text(
                AnnotatedString.fromHtml(card.comment),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fadingEdges(scrollState)
                    .padding(top = 20.dp),
                style = AppTheme.typography.body1.copy(fontFamily = DMSansVazir),
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: Painter,
    backgroundColor: Color,
    borderColor: Color,
    iconTint: Color,
    iconSize: Dp = 24.dp,
    size: Dp = 73.dp,
    shadow: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .applyIf(shadow) {
                dropShadow(
                    CircleShape,
                    Shadow(radius = 9.dp, color = Color(0x99676767))
                )
            }
            .border(2.dp, color = borderColor, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
    }
}

val motivationalPhrases = listOf(
    "Small steps build big knowledge.",
    "Consistency turns effort into mastery.",
    "One card today, wisdom tomorrow.",
    "Tiny progress beats no progress.",
    "Every repetition strengthens memory.",
    "Learning grows through patience.",
    "Steady effort shapes sharp minds.",
    "Slow growth is lasting growth.",
    "Focus turns minutes into mastery.",
    "Bit by bit, you’re building brilliance.",
    "Each review plants a seed.",
    "Every card adds a brick of knowledge.",
    "Quiet effort builds loud results.",
    "Little actions form great habits.",
    "Persistence makes knowledge stick.",
    "Discipline feeds understanding.",
    "Daily learning shapes destiny.",
    "Knowledge grows where effort flows.",
    "Small moves, big impact.",
    "Tiny lessons create vast wisdom.",
    "Learning loves repetition.",
    "One step at a time creates mastery.",
    "Growth hides in consistency.",
    "Each card counts, no matter how small.",
    "Patience builds the strongest minds.",
    "Repeat, reflect, remember.",
    "Focus makes knowledge bloom.",
    "Wisdom is built, not bought.",
    "Keep feeding your curiosity.",
    "Simple effort compounds over time.",
    "Every review strengthens the path.",
    "Mastery is made of small moments.",
    "Quiet study, loud progress.",
    "Knowledge rewards the patient.",
    "Consistency carves clarity.",
    "Repetition writes memory deep.",
    "Little focus, lasting change.",
    "Step by step, the mind evolves.",
    "A minute of focus beats an hour of distraction.",
    "Growth is hidden in small habits.",
    "Every step is a story of progress.",
    "Small seeds grow tall trees.",
    "Practice shapes understanding.",
    "Little efforts echo through time.",
    "Each card is a doorway to mastery.",
    "Slow and steady builds forever.",
    "Tiny focus, huge progress.",
    "Mastery loves quiet effort.",
    "Knowledge unfolds with time.",
    "Focus is the bridge to mastery.",
    "Revisit to remember, review to rise.",
    "Repetition turns effort into ease.",
    "Consistency is the hidden teacher.",
    "Each card is a quiet victory.",
    "Patience is power in learning.",
    "Small steps reveal great truths.",
    "Steady rhythm, growing mind.",
    "Persistence paints the picture of progress.",
    "Each repetition is a brushstroke of mastery.",
    "Slow effort beats fast burnout.",
    "Wisdom grows from simple habits.",
    "Every review is an act of self-improvement.",
    "Small habits build sharp minds.",
    "You learn by staying, not rushing.",
    "Bit by bit, understanding deepens.",
    "Learning is built, one review at a time.",
    "Focus turns struggle into skill.",
    "Tiny focus moments create mastery.",
    "Little progress lights the path forward.",
    "Each answer makes you stronger.",
    "Step by step, you learn to soar.",
    "The smallest effort moves you forward.",
    "Mastery is quiet persistence.",
    "Keep going, the growth is happening.",
    "Every review rewires your mind.",
    "Clarity comes through repetition.",
    "Small habits make strong learners.",
    "You’re training your future mind.",
    "Repetition refines memory.",
    "Keep watering your knowledge tree.",
    "Patience builds understanding.",
    "Tiny moments grow mighty minds.",
    "Every repetition plants confidence.",
    "One card at a time builds mastery.",
    "Learning is a quiet revolution.",
    "Step, review, repeat, rise.",
    "Small actions, great direction.",
    "Steady focus makes deep roots.",
    "Bit by bit, brilliance grows.",
    "Knowledge compounds quietly.",
    "Every effort adds up.",
    "Small acts shape great learners.",
    "You’re sculpting wisdom slowly.",
    "Little focus, lasting clarity.",
    "Consistency is your silent superpower.",
    "Daily effort builds deep knowledge.",
    "Repetition fuels understanding.",
    "Small moments, lifelong skills.",
    "Each session shapes your thinking.",
    "Slow progress is still progress.",
    "Every tiny effort strengthens you.",
    "Small steps today, big leaps tomorrow."
)
