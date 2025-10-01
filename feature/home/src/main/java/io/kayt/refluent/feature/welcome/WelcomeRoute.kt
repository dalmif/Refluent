package io.kayt.refluent.feature.welcome

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

fun NavGraphBuilder.welcome(
    onNextButtonClick: () -> Unit
) {
    composable<WelcomeRoute> {
        WelcomeScreen(
            onNextButtonClick = onNextButtonClick
        )
    }
}