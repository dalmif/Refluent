package io.kayt.refluent.feature.home.adddeck

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Serializable
data object AddDeckRoute

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addDeck(navController: NavController) {
    dialog<AddDeckRoute> {

        val modalState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ) { it != SheetValue.Hidden }
        ModalBottomSheet(
            sheetState = modalState,
            shape = RoundedCornerShape(38.dp),
            dragHandle = {},
            containerColor = Color.Transparent,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .statusBarsPadding(),
            onDismissRequest = {
                navController.popBackStack()
            }
        ) {
            val scope = rememberCoroutineScope()
            AddDeckModal(
                modifier = Modifier.padding(bottom = 10.dp),
                onBackClick = {
                    scope.launch {
                        modalState.hide()
                        navController.popBackStack()
                    }
                })
        }
    }
}

fun NavController.navigateToAddDeck() {
    navigate(AddDeckRoute)
}