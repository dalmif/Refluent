package io.kayt.refluent.feature.deck

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DeckRoute

fun NavGraphBuilder.deck() {
    composable<DeckRoute> {
        DeckScreen()
    }
}
fun NavController.navigateToDeck() {
    navigate(DeckRoute)
}