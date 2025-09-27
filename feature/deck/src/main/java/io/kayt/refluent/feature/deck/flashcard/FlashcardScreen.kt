package io.kayt.refluent.feature.deck.flashcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items
import io.kayt.core.model.Card
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.DashedDivider
import io.kayt.refluent.core.ui.component.fadingEdges
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FlashcardScreen(
        state = state,
        onSwipeRight = viewModel::markCardAsGood,
        onSwipeLeft = viewModel::markCardAsBad,
    )
}

@Composable
private fun FlashcardScreen(
    state: FlashcardUiState,
    onSwipeRight: (Card) -> Unit,
    onSwipeLeft: (Card) -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFDDCAA), Color(0xFFECDBDA))
                    )
                )
                .background(Color.Black.copy(alpha = 0.1f))
                .padding(innerPadding)
        ) {
            when (state) {
                is FlashcardUiState.Loading -> {
                    // Loading state
                }

                is FlashcardUiState.Success -> {
                    val flipState = remember { mutableStateOf(0 to false) }
                    val swipeableState = rememberSwipeableCardsState(
                        itemCount = { state.cards.size }
                    )

                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(vertical = 20.dp)
                            .weight(1f)
                    ) {
                        val cardVisible =
                            remember { derivedStateOf { swipeableState.currentCardIndex != state.cards.size } }
                        if (cardVisible.value) {
                            LazySwipeableCards(
                                state = swipeableState,
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
                                    SwipeableCard(
                                        card = card,
                                        isOnTop = swipeableState.currentCardIndex >= index,
                                        isVisible = swipeableState.currentCardIndex + 1 >= index,
                                        isFront = flipState.value.second.not()
                                            .takeIf { flipState.value.first == index } ?: true,
                                        onFlipRequested = {
                                            flipState.value =
                                                if (flipState.value.first == index) index to !flipState.value.second
                                                else index to true
                                        },
                                    )
                                }
                            }
                        } else {
                            Text("Nothing Here")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 60.dp),
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
                                println("mmd here is the card swuiped size: ${state.cards.size} and index: ${swipeableState.currentCardIndex}")
                                if (state.cards.size > swipeableState.currentCardIndex) {
                                    println("mmd here DONE: ${state.cards.size} and index: ${swipeableState.currentCardIndex}")
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
                                    if (flipState.value.first == swipeableState.currentCardIndex) swipeableState.currentCardIndex to !flipState.value.second
                                    else swipeableState.currentCardIndex to true
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
    isVisible: Boolean
) {
    val targetAngle = if (isFront) 0f else -180f
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
                    modifier = Modifier.fillMaxSize(),
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
                            text = card.phonetic,
                            style = AppTheme.typography.body1,
                            color = Color(0xFFA4A4A4)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(R.drawable.ic_light_sound_wave),
                            contentDescription = "Play audio",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { },
                            tint = Color(0xFFB2B2B2)
                        )
                    }
                }
            } else {
                Column(
                    Modifier.graphicsLayer {
                        rotationY = 180f
                    },
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
            }

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