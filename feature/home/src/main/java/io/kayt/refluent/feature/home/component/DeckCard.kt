package io.kayt.refluent.feature.home.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kayt.core.model.Deck
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.core.ui.theme.AppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DeckCard(
    deck: Deck,
    onClick: () -> Unit,
    onStudyClick: () -> Unit,
    deckId: Long,
    modifier: Modifier = Modifier
) {
    with(LocalSharedTransitionScope.current) {
        Column(
            modifier = modifier
                .sharedBounds(
                    rememberSharedContentState(key = "deck_background_$deckId"),
                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(deck.colors.first), Color(deck.colors.second))
                    )
                )
                .padding(start = 21.dp, end = 16.dp)
                .padding(top = 38.dp, bottom = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) {
                    Text(
                        deck.name.uppercase(),
                        style = AppTheme.typography.headline1,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "deck_title_$deckId"),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        )
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = deck.dueCards.toString(),
                        style = AppTheme.typography.subhead,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "deck_due_cards_$deckId"),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        )
                    )
                    Text(
                        text = "due for reviews",
                        style = AppTheme.typography.body1,
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
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "deck_total_cards_$deckId"),
                        animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                    )
                )
                Row(
                    Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onStudyClick)
                        .background(Color(0x45F9C959))
                        .padding(vertical = 7.dp, horizontal = 14.dp)
                ) {
                    Text(
                        text = "Click to Study",
                        style = AppTheme.typography.body1
                    )
                    Spacer(Modifier.width(3.dp))
                    Icon(
                        painterResource(R.drawable.icon_arrow_right),
                        contentDescription = null
                    )
                }
            }
        }
    }
}