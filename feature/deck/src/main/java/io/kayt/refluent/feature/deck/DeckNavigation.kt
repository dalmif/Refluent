package io.kayt.refluent.feature.deck

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class DeckRoute(val deckId: Long)

fun NavGraphBuilder.deck(
    onAddCardClick: (deckId: Long) -> Unit,
    onStudyClick: () -> Unit,
) {
    composable<DeckRoute> {
        DeckScreen(
            onAddCardClick = { onAddCardClick(it.toRoute<DeckRoute>().deckId) },
            onStudyClick = onStudyClick
        )
    }
}

fun NavController.navigateToDeck(deckId: Long) {
    navigate(DeckRoute(deckId))
}