package io.kayt.refluent.feature.deck.addcard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.button.SecondaryBigButton
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.colors.Purple1
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir
import io.kayt.refluent.feature.deck.component.RichTextStyleRow

@Composable
fun AddCardScreen(viewModel: AddCardViewModel = hiltViewModel()) {
    AddCardScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCardScreen() {
    Scaffold { paddingValues ->
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 17.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 15.dp)
            ) {
                Row {
                    SideTextColumn("Front Side")
                    Spacer(Modifier.width(10.dp))
                    SideTextColumn("Back Side")
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "Comment",
                    style = AppTheme.typography.textFieldTitle,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                )

                val state = rememberRichTextState()

                var active by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .onFocusEvent({
                            active = it.hasFocus
                        })
                        .border(
                            if (!active) 1.dp else 2.dp,
                            color = if (!active) Color(0xFFBEBEBE) else Purple1,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    RichTextStyleRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        state = state,
                    )
                    HorizontalDivider(color = Color(0xFFF6F6F6))
                    OutlinedRichTextEditor(
                        state = state,
                        colors = RichTextEditorDefaults.outlinedRichTextEditorColors(
                            focusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        textStyle = AppTheme.typography.body2.copy(fontFamily = DMSansVazir),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp),
                        placeholder = {
                            Text(
                                "Additional explanation that will shown on the back side",
                                style = AppTheme.typography.body2,
                            )
                        }
                    )
                }
                Text(
                    text = "Generate comment by AI",
                    style = AppTheme.typography.textFieldTitle,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 16.dp),
                )
                AiButton(text = "Explain the front side")
                Spacer(Modifier.height(7.dp))
                AiButton(text = "Make some example from front side")
                Spacer(Modifier.height(7.dp))
                AiButton(text = "Add new AI comment generator")

                Spacer(Modifier.height(100.dp))
            }

            SecondaryBigButton(
                {}, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text("Add Card")
            }
        }
    }
}

@Composable
private fun AiButton(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(
                1.dp,
                brush = Brush.horizontalGradient(
                    0f to Color(0xFFCA40D6),
                    1f to Color(0xFF4677E9)
                ),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable {}
            .padding(vertical = 8.dp, horizontal = 14.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_ai_model),
            contentDescription = null,
            tint = Color(0xFFCA40D6),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text,
            style = AppTheme.typography.body2.copy(
                brush = Brush.horizontalGradient(
                    0f to Color(0xFFCA40D6),
                    1f to Color(0xFF4677E9)
                )
            ),
        )
    }
}

@Composable
private fun RowScope.SideTextColumn(title: String) {
    Column(modifier = Modifier.weight(0.5f)) {
        Text(
            text = title,
            style = AppTheme.typography.textFieldTitle,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(5.dp))
        var text by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }
        BasicTextField(
            value = text,
            textStyle = AppTheme.typography.body2.copy(fontFamily = DMSansVazir, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .onFocusEvent({
                    active = it.hasFocus
                })
                .aspectRatio(1.33f)
                .border(
                    if (!active) 1.dp else 2.dp,
                    color = if (!active) Color(0xFFBEBEBE) else Purple1,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 10.dp),
            onValueChange = {
                text = it
            },
            decorationBox = {
                it()
                if (text.isEmpty()) {
                    Text(
                        text = "Write your text here",
                        style = AppTheme.typography.body2,
                        color = Color(0xFF888888)
                    )
                }
            }
        )
    }
}