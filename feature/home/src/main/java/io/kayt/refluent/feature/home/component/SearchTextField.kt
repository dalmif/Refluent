package io.kayt.refluent.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.theme.AppTheme

@Composable
fun SearchTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), propagateMinConstraints = true) {
        TextField(
            modifier = Modifier
                .height(55.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        0f to Color(0xFFD7D7D7),
                        1f to Color.Transparent
                    ),
                    shape = CircleShape
                )
                .shadow(
                    4.dp,
                    CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color.Black.copy(0.2f)
                )
                .background(Color.White)
                .padding(end = 80.dp),
            onValueChange = onValueChange,
            value = value,
            isError = false,
            singleLine = true,
            maxLines = 1,
            minLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Sentences
            ),
            shape = CircleShape,
            placeholder = {
                Text(
                    "Search your cards",
                    style = AppTheme.typography.body1
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_search_magnifier),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(17.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                // Placeholder
                unfocusedPlaceholderColor = AppTheme.colors.textSecondary,
                errorPlaceholderColor = AppTheme.colors.textSecondary,
                focusedPlaceholderColor = AppTheme.colors.textSecondary,
                disabledPlaceholderColor = AppTheme.colors.textSecondary,

                // Text
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary,
                disabledTextColor = AppTheme.colors.textPrimary,
                errorTextColor = AppTheme.colors.textPrimary,

                // Background
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // Error
                errorLabelColor = AppTheme.colors.error,

                // Cursor
                cursorColor = AppTheme.colors.textPrimary,
                errorCursorColor = AppTheme.colors.textPrimary,

                // Hide the line under the text field
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            )
        )
        Image(
            painter = painterResource(R.drawable.search_lay_down),
            contentDescription = null,

            modifier = Modifier
                .height(50.dp)
                .width(65.dp)
                .padding(end = 16.dp)
                .align(Alignment.CenterEnd)
                .wrapContentSize(align = Alignment.CenterEnd)
        )
    }
}