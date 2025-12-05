package io.kayt.refluent.feature.home.liveedit

import android.content.ClipData
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun LiveEditModal(
    viewModel: LiveEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LiveEditContent(
        state,
        onConnectClick = viewModel::connect,
        onDisconnectClick = viewModel::disconnect,
    )

    // Keep the screen on to not let the socket to die
    val activity = LocalActivity.current
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
private fun LiveEditContent(
    state: LiveEditUiState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 27.dp)
    ) {
        Text(
            "Share for live editing",
            style = AppTheme.typography.headline2.copy(fontSize = 22.sp),
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "Tap on “Start Sharing” button to get a temporary code to access your decks in your browser. You or your teacher can edit or add cards from any browser.",
            style = AppTheme.typography.body1,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.fillMaxWidth()
        )
        val clipboardManager = LocalClipboard.current
        val coroutine = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .border(1.dp, color = AppTheme.colors.backgroundGrey, shape = CircleShape)
                .clip(CircleShape)
                .clickable(enabled = state is LiveEditUiState.Connected, onClick = {
                    (state as? LiveEditUiState.Connected)?.connectionCode?.let {
                        coroutine.launch {
                            clipboardManager.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Live Edit Key",
                                        it
                                    )
                                )
                            )
                        }
                    }
                })
                .padding(vertical = 11.dp, horizontal = 20.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.brainy_laptop),
                contentDescription = null
            )
            AnimatedContent(state, contentKey = { it is LiveEditUiState.Connected }) {
                val stars = "******"
                val detail = if (it is LiveEditUiState.Connected) it.connectionCode else stars
                Text(
                    detail,
                    style = AppTheme.typography.large,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .applyIf(detail == stars) { offset(y = 7.dp) }

                )
            }
        }
        Text(
            AnnotatedString.fromHtml(
                "Go to <a href=\"https://live.refluent.app\">live.refluent.app</a>, enter the code, and you’ll be able to access your deck. You can leave this screen, but the app must stay open for web access.",
                linkStyles = TextLinkStyles(
                    style = SpanStyle(
                        color = AppTheme.colors.textHighlight
                    )
                )
            ),
            style = AppTheme.typography.body1.copy(fontSize = 14.sp),
            color = AppTheme.colors.textSecondary.copy(alpha = 0.8f),
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton(
            {
                when (state) {
                    is LiveEditUiState.Connected -> onDisconnectClick()
                    LiveEditUiState.Disconnected -> onConnectClick()
                    LiveEditUiState.Connecting -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 10.dp)
        ) {
            if (state is LiveEditUiState.Connecting) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.onBackgroundDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Text(
                    if (state is LiveEditUiState.Connected) "Stop Sharing" else "Start Sharing"
                )
            }
        }
    }
}

@Preview
@Composable
private fun LiveEditModalPreview() {
    AppTheme {
        LiveEditModal()
    }
}