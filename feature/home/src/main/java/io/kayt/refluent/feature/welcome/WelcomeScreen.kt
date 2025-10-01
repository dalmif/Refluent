package io.kayt.refluent.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.colors.Purple1
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir
import io.kayt.refluent.core.ui.theme.typography.LifeSaver

@Composable
fun WelcomeScreen(
    onNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    WelcomeScreenBackground(
        modifier = Modifier
            .fillMaxSize()
//            .imePadding()
    ) {
        Scaffold(containerColor = Color.Transparent) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Bonjour",
                    style = AppTheme.typography.headline1.copy(
                        fontWeight = FontWeight.Light,
                        fontSize = 45.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .width(176.dp)
                ) {
                    Spacer(
                        Modifier
                            .offset(x = 7.dp, y = 4.dp)
                            .background(Color(0xFFF5D923), CircleShape)
                            .size(116.dp, 96.dp)
                    )
                    Image(painter = painterResource(R.drawable.greeting), null)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Welcome to Refluent\n" +
                            "My name is Brainy\n" +
                            "What is your name?",
                    fontFamily = LifeSaver,
                    fontSize = 24.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 50.dp),
                    color = Color(0xFF4F4224)
                )
                CustomTextField(
                    value = "",
                    hint = "Enter your first name",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .padding(horizontal = 30.dp)
                        .height(66.dp)
                )
                PrimaryButton(
                    onClick = onNextButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .padding(horizontal = 30.dp)
                        .height(71.dp)
                ) {
                    Text("Let's get started")
                }
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}


@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    phonetic: String? = null,
) {
    var active by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        textStyle = AppTheme.typography.body2.copy(
            fontFamily = DMSansVazir,
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier
            .onFocusEvent({
                active = it.hasFocus
            })
            .dropShadow(
                shape = CircleShape,
                shadow = Shadow(
                    radius = 0.dp,
                    color = Color.Black,
                    spread = 0.dp,
                    offset = DpOffset(0.dp, 1.dp),
                )
            )
            .border(
                if (!active) 1.dp else 2.dp,
                color = if (!active) Color.Black else Purple1,
                shape = CircleShape
            )
            .background(Color.White, CircleShape)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        ),
        decorationBox = {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                it()
                if (value.isEmpty() && hint?.isNotEmpty() == true) {
                    Text(
                        text = hint,
                        style = AppTheme.typography.body2,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    )
}

@Composable
private fun WelcomeScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .background(AppTheme.colors.background)
            .drawBehind {
                drawCircle(
                    Color(0xFFFDC679),
                    radius = size.width * 0.92f,
                    center = Offset(x = size.width, y = 0f)
                )
                drawCircle(
                    Color(0xFFFFF9D4),
                    radius = (size.width / 2) - 40.dp.roundToPx(),
                    center = Offset(x = 0f, y = size.height / 2)
                )
            }) {
        Box(
            Modifier
                .clip(RoundedCornerShape(bottomStart = 1000.dp))
                .fillMaxWidth(0.92f)
                .aspectRatio(1f)
                .align(AbsoluteAlignment.TopRight)
        ) {
            Image(
                painter = painterResource(R.drawable.greeting_flags), null,
                modifier = Modifier
                    .padding(start = 100.dp)
                    .size(230.dp)
                    .align(AbsoluteAlignment.BottomLeft)
            )
        }
        content()
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    WelcomeScreen(
        onNextButtonClick = {},
    )
}