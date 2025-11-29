package io.kayt.refluent.feature.home.liveedit

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kayt.core.model.util.applyIf
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.theme.AppTheme

@Composable
fun LiveEditModal() {
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
        Row(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .border(1.dp, color = AppTheme.colors.backgroundGrey, shape = CircleShape)
                .padding(vertical = 11.dp, horizontal = 20.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.brainy_laptop),
                contentDescription = null
            )
            val stars = "******"
            val detail = stars
            Text(
                detail,
                style = AppTheme.typography.large,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .applyIf(detail == stars) { offset(y = 7.dp) }

            )
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
            {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 10.dp)
        ) {
            Text("Start Sharing")
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