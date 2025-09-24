package io.kayt.refluent.feature.deck.flashcard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


@Serializable
data class FlashcardRoute(val deckId: Long)

fun NavGraphBuilder.flashCard(
) {
    composable<FlashcardRoute> {
        FlashcardScreen()
    }
}

fun NavController.navigateToFlashcard(deckId: Long) {
    navigate(FlashcardRoute(deckId))
}