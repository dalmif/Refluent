package io.kayt.refluent.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.kayt.refluent.core.ui.component.LocalNavAnimatedVisibilityScope
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.home(
    onAddDeckClick: () -> Unit,
    onDeckClick: (Long) -> Unit
) {
    composable<HomeRoute> {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            HomeScreen(
                onAddDeckClick = onAddDeckClick,
                onDeckClick = onDeckClick
            )
        }
    }
}