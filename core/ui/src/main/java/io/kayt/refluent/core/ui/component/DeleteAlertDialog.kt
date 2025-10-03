package io.kayt.refluent.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteAlertDialog(
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = "Confirm deletion",
        description = "This action cannot be undone. Please confirm before proceeding.",
        secondaryButtonText = "Cancel",
        onDismissRequest = onDismissRequest,
        primaryButtonText = "Delete",
        primaryButtonAction = onDeleteClick,
        secondaryButtonAction = onDismissRequest,
        modifier = modifier
    )
}