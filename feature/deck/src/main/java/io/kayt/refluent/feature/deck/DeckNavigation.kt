package io.kayt.refluent.feature.deck

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DeckRoute

fun NavGraphBuilder.deck(
    onAddCardClick: () -> Unit,
    onStudyClick: () -> Unit,
) {
    composable<DeckRoute> {
        DeckScreen(
            onAddCardClick = onAddCardClick,
            onStudyClick = onStudyClick
        )
    }
}

fun NavController.navigateToDeck() {
    navigate(DeckRoute)
}