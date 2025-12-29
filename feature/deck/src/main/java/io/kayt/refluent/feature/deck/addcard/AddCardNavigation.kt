package io.kayt.refluent.feature.deck.addcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class AddCardRoute(val deckId: Long, val editingCardId: Long? = null)

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addCard(navController: NavController) {
    dialog<AddCardRoute> {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden }
        )
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet(
            containerColor = AppTheme.colors.background,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 20.dp),
            sheetState = state,
            sheetGesturesEnabled = true,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = true,
                shouldDismissOnClickOutside = false
            ),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight()
                            .aspectRatio(1.2f)
                            .clip(RoundedCornerShape(bottomStart = 11.dp))
                            .background(if (AppTheme.isDark) Color(0xFF2A2A2A) else Color(0xFFF3F3F3))
                            .clickable(
                                onClick = {
                                    coroutineScope.launch {
                                        state.hide()
                                        navController.popBackStack()
                                    }
                                }
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_close_general),
                            null,
                            tint = if (AppTheme.isDark) Color(0xFFA5A5A5) else Color(0xFF645315)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                            .clip(CircleShape)
                            .width(50.dp)
                            .height(6.dp)
                            .background(
                                if (AppTheme.isDark) Color(0xFF404040)
                                else Color(0xFFDEDEDE)
                            )
                    )
                }
            },
            onDismissRequest = {
                navController.popBackStack()
            }
        ) {
            Box(modifier = Modifier) {
                AddCardScreen(
                    onBackClick = {
                        coroutineScope.launch {
                            state.hide()
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

fun NavController.navigateToAddCard(deckId: Long) {
    navigate(AddCardRoute(deckId))
}

fun NavController.navigateToEditCard(cardId: Long) {
    navigate(AddCardRoute(0, cardId))
}
