package io.kayt.refluent.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.home(
    onAddDeckClick : () -> Unit,
    onDeckClick : (Int) -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onAddDeckClick = onAddDeckClick,
            onDeckClick = onDeckClick
        )
    }
}