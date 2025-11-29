package io.kayt.refluent.feature.home.liveedit

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.serialization.Serializable


@Serializable
data object LiveEditRoute

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.liveEdit(navController: NavController) {
    dialog<LiveEditRoute> {
        val modalState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        ModalBottomSheet(
            sheetState = modalState,
            shape = RoundedCornerShape(38.dp),
            containerColor = AppTheme.colors.background,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            onDismissRequest = {
                navController.popBackStack()
            }
        ) {
            LiveEditModal()
        }
    }
}

fun NavController.navigateToLiveEdit() {
    navigate(LiveEditRoute)
}