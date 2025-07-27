package io.kayt.refluent.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.theme.AppTheme

@Composable
fun DeckCard(
    onClick: () -> Unit,
    onStudyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(Color(0xFFBBE2A8))
            .padding(start = 21.dp, end = 16.dp)
            .padding(top = 38.dp, bottom = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                Text(
                    "English with Kiana".uppercase(),
                    style = AppTheme.typography.headline1
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "324",
                    style = AppTheme.typography.subhead
                )
                Text(
                    text = "due for reviews",
                    style = AppTheme.typography.body1
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "2510 cards",
                style = AppTheme.typography.body2
            )
            Row(
                Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onStudyClick)
                    .background(Color(0x45F9C959))
                    .padding(vertical = 7.dp, horizontal = 14.dp)
            ) {
                Text(
                    text = "Click to Study",
                    style = AppTheme.typography.body1
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    painterResource(R.drawable.icon_arrow_right),
                    contentDescription = null
                )
            }
        }
    }
}