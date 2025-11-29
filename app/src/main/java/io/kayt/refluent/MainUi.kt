package io.kayt.refluent

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.feature.deck.addcard.addCard
import io.kayt.refluent.feature.deck.addcard.navigateToAddCard
import io.kayt.refluent.feature.deck.addcard.navigateToEditCard
import io.kayt.refluent.feature.deck.deck
import io.kayt.refluent.feature.deck.flashcard.flashCard
import io.kayt.refluent.feature.deck.flashcard.navigateToFlashcard
import io.kayt.refluent.feature.deck.navigateToDeck
import io.kayt.refluent.feature.home.HomeRoute
import io.kayt.refluent.feature.home.adddeck.addDeck
import io.kayt.refluent.feature.home.adddeck.navigateToAddDeck
import io.kayt.refluent.feature.home.adddeck.navigateToEditDeck
import io.kayt.refluent.feature.home.home
import io.kayt.refluent.feature.home.liveedit.liveEdit
import io.kayt.refluent.feature.home.liveedit.navigateToLiveEdit
import io.kayt.refluent.feature.home.navigateToHome

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainUi() {
    val navController = rememberNavController()
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute
            ) {
                home(
                    onAddDeckClick = {
                        navController.navigateToAddDeck(it)
                    },
                    onDeckClick = {
                        navController.navigateToDeck(it)
                    },
                    onDeckEditClick = {
                        navController.navigateToEditDeck(it)
                    },
                    onStudyClick = {
                        navController.navigateToFlashcard(it)
                    },
                    onLiveEditClick = {
                        navController.navigateToLiveEdit()
                    }
                )
                deck(
                    onAddCardClick = {
                        navController.navigateToAddCard(it)
                    },
                    onStudyClick = {
                        navController.navigateToFlashcard(it)
                    },
                    onEditCardClick = {
                        navController.navigateToEditCard(it)
                    }
                )
                flashCard(
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
                addCard(navController)
                addDeck(navController)
                liveEdit(navController)
            }
        }
    }
}
