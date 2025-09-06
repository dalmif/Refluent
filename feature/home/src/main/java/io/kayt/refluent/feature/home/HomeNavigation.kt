package io.kayt.refluent.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.home(
    onDeckClick : (Int) -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onDeckClick = onDeckClick
        )
    }
}