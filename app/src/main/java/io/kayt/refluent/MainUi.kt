package io.kayt.refluent

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.kayt.refluent.feature.deck.deck
import io.kayt.refluent.feature.deck.navigateToDeck
import io.kayt.refluent.feature.deck.addcard.addCard
import io.kayt.refluent.feature.deck.addcard.navigateToAddCard
import io.kayt.refluent.feature.home.HomeRoute
import io.kayt.refluent.feature.home.home

@Composable
fun MainUi() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        home(
            onDeckClick = {
                navController.navigateToDeck()
            }
        )
        deck(
            onAddCardClick = {
                navController.navigateToAddCard()
            },
            onStudyClick = {}
        )
        addCard(navController)
    }
}