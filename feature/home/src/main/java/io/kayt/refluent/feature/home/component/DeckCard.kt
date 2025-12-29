package io.kayt.refluent.feature.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kayt.core.model.Deck
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DeckCard(
    deck: Deck,
    onClick: () -> Unit,
    onStudyClick: () -> Unit,
    onLongPress: () -> Unit,
    deckId: Long,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val lastValueOfAnimation = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isPressed) {
        if (isPressed) {
            val start = withFrameNanos { it }
            val startedValue = lastValueOfAnimation.floatValue
            while (currentCoroutineContext().isActive) {
                val now = withFrameNanos { it }
                val elapsedMs = (now - start) / 1_000_000
                val currentValue = (startedValue + (elapsedMs / 1_000f)).coerceAtMost(1f)
                if (scaleAnim.value != currentValue && currentValue == 1.0f) {
                    onLongPress()
                }
                scaleAnim.snapTo(currentValue)
            }
        } else {
            scaleAnim.animateTo(0f, tween(1000)) {
                lastValueOfAnimation.floatValue = value
            }
        }
    }

    val colorPoint = scaleAnim.value


    with(LocalSharedTransitionScope.current) {
        Column(
            modifier = modifier
                .sharedBounds(
                    rememberSharedContentState(key = "deck_background_$deckId"),
                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .applyIf(!AppTheme.isDark) {
                    background(
                        Brush.horizontalGradient(
                            colorPoint to Color(deck.colors.first),
                            1f to Color(deck.colors.second)
                        )
                    )
                }
                .applyIf(AppTheme.isDark) {
                    background(
                        AppTheme.colors.cardBackground
                    )
                }
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                    onLongClick = {}
                )
                .padding(start = 21.dp, end = 16.dp)
                .padding(top = 38.dp, bottom = 22.dp)
        ) {
            var timePointingIcon by remember { mutableStateOf(false) }
            val contentColor = if (AppTheme.isDark) Color(deck.colors.first) else AppTheme.colors.textPrimary
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(Modifier.weight(1f)) {
                    Text(
                        deck.name.uppercase(),
                        style = AppTheme.typography.headline1,
                        color = contentColor,
                        maxLines = 4,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "deck_title_$deckId"),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        )
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "deck_due_cards_$deckId"),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        )
                    ) {
                        if (deck.totalCards == 0 || deck.dueCards > 0) {
                            Text(
                                text = deck.dueCards.toString(),
                                style = AppTheme.typography.subhead,
                                color = contentColor,
                            )
                        } else {
                            AnimatedContent(
                                timePointingIcon,
                                transitionSpec = {
                                    slideInHorizontally { it } + fadeIn() togetherWith
                                            slideOutHorizontally { it } + fadeOut()
                                }) {
                                Icon(
                                    if (!it) painterResource(R.drawable.brainstorming)
                                    else painterResource(R.drawable.no_card_yet),
                                    null,
                                    modifier = Modifier.size(80.dp),
                                    tint = AppTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                    Text(
                        text =
                            if (deck.totalCards == 0 || deck.dueCards > 0) "due for reviews"
                            else "All caught up!",
                        style = AppTheme.typography.body1,
                        color = contentColor,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "deck_due_text_$deckId"),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        )
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${deck.totalCards} cards",
                    style = AppTheme.typography.body2,
                    color = contentColor,
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "deck_total_cards_$deckId"),
                        animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                    )
                )
                val scope = rememberCoroutineScope()
                Row(
                    Modifier
                        .clip(CircleShape)
                        .clickable(onClick = {
                            if (deck.dueCards > 0) {
                                onStudyClick()
                            } else {
                                if (deck.totalCards > 0) {
                                    scope.launch {
                                        timePointingIcon = true
                                        delay(2000)
                                        timePointingIcon = false
                                    }
                                } else {
                                    // if this is a new deck with no card, clicking this button
                                    // should treated as clicking the whole
                                    onClick()
                                }
                            }
                        })
                        .background(
                            if (AppTheme.isDark)
                            contentColor.copy(alpha = 0.34f)
                            else Color(0x45F9C959)
                        )
                        .padding(vertical = 7.dp, horizontal = 14.dp)
                ) {
                    Text(
                        text = if (deck.dueCards == 0 && deck.totalCards == 0)
                            "Go to Deck"
                        else "Let's review",
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.textPrimary,
                    )
                    Spacer(Modifier.width(3.dp))
                    Icon(
                        painterResource(R.drawable.icon_arrow_right),
                        contentDescription = null,
                        tint = AppTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}