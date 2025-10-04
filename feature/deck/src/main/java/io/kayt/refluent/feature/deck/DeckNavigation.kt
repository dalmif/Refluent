package io.kayt.refluent.feature.deck

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import kotlinx.serialization.Serializable

@Serializable
data class DeckRoute(val deckId: Long)

fun NavGraphBuilder.deck(
    onAddCardClick: (deckId: Long) -> Unit,
    onStudyClick: (deckId: Long) -> Unit,
    onEditCardClick: (cardId: Long) -> Unit,
) {
    composable<DeckRoute> { route ->
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            DeckScreen(
                onAddCardClick = { onAddCardClick(route.toRoute<DeckRoute>().deckId) },
                onStudyClick = { onStudyClick(route.toRoute<DeckRoute>().deckId) },
                onEditCardClick = onEditCardClick
            )
        }
    }
}

fun NavController.navigateToDeck(deckId: Long) {
    navigate(DeckRoute(deckId))
}