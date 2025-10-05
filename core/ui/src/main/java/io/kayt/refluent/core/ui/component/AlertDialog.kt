package io.kayt.refluent.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kayt.refluent.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    title: String,
    description: String,
    primaryButtonText: String,
    secondaryButtonText: String?,
    primaryButtonAction: () -> Unit,
    secondaryButtonAction: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                color = AppTheme.colors.background,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Start,
                        style = AppTheme.typography.headline3.copy(
                            color = AppTheme.colors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        textAlign = TextAlign.Start,
                        style = AppTheme.typography.body2.copy(
                            color = AppTheme.colors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        secondaryButtonText?.let {
                            TextButton(onClick = secondaryButtonAction) {
                                Text(
                                    text = secondaryButtonText,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = AppTheme.colors.ctaPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        TextButton(onClick = primaryButtonAction) {
                            Text(
                                text = primaryButtonText,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = AppTheme.colors.ctaPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AlertDialogPreview() {
    AppTheme {
        AlertDialog(
            title = "Basic dialog title",
            description = "A dialog is a type of modal window that appears in front of app content to provide " +
                "critical information, or prompt for a decision to be made.",
            primaryButtonText = "Action 1",
            secondaryButtonText = "Action 2",
            primaryButtonAction = {},
            secondaryButtonAction = {},
            onDismissRequest = {}
        )
    }
}
