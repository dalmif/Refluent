package io.kayt.refluent.feature.deck.addcard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.serialization.Serializable

@Serializable
data object AddCardRoute

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addCard(navController: NavController) {
    dialog<AddCardRoute> {
        ModalBottomSheet(
            containerColor = AppTheme.colors.background,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 20.dp),
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }),
            onDismissRequest = {
                navController.popBackStack()
            }) {
            AddCardScreen()
        }
    }
}

fun NavController.navigateToAddCard() {
    navigate(AddCardRoute)
}