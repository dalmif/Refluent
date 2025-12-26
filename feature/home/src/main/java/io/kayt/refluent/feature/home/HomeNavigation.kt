package io.kayt.refluent.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.kayt.core.model.DarkModeType
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.home(
    onAddDeckClick: (deckCount : Int) -> Unit,
    onDeckClick: (Long) -> Unit,
    onDeckEditClick: (Long) -> Unit,
    onStudyClick : (Long) -> Unit,
    onLiveEditClick : () -> Unit,
    onDarkModeClick : () -> Unit,
    darkMode: DarkModeType
) {
    composable<HomeRoute> {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            HomeScreen(
                onAddDeckClick = onAddDeckClick,
                onDeckClick = onDeckClick,
                onDeckEditClick = onDeckEditClick,
                onStudyClick = onStudyClick,
                onLiveEditClick = onLiveEditClick,
                onDarkModeClick = onDarkModeClick,
                darkMode = darkMode
            )
        }
    }
}

fun NavController.navigateToHome() {
    navigate(HomeRoute)
}