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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.button.SecondaryBigButton
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.colors.Purple1
import io.kayt.refluent.core.ui.theme.typography.Charis
import io.kayt.refluent.core.ui.theme.typography.DMSansVazir
import io.kayt.refluent.feature.deck.component.RichTextStyleRow

@Composable
fun AddCardScreen(
    onAddClick: () -> Unit,
    viewModel: AddCardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val phonetic by viewModel.phonetic.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            snackbarHostState.showSnackbar("Something went wrong while generating with AI. Please check your network connection and try again.")
        }
    }
    AddCardScreen(
        state = state,
        phonetic = phonetic,
        snackbarHostState = snackbarHostState,
        onFrontSideChange = viewModel::onFrontSideChange,
        onBackSideChange = viewModel::onBackSideChange,
        onAiGenerateClick = viewModel::onAiGenerateClick,
        onAddCardButton = {
            viewModel.onAddCardButton()
            onAddClick()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCardScreen(
    state: AddCardUiState,
    phonetic: String?,
    snackbarHostState: SnackbarHostState,
    onFrontSideChange: (String) -> Unit,
    onBackSideChange: (String) -> Unit,
    onAiGenerateClick: (AiGenerate) -> Unit,
    onAddCardButton: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
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
                    SideTextColumn(
                        title = "Front Side",
                        value = state.frontSide,
                        phonetic = phonetic,
                        onValueChange = onFrontSideChange
                    )
                    Spacer(Modifier.width(10.dp))
                    SideTextColumn(
                        title = "Back Side",
                        value = state.backSide,
                        onValueChange = onBackSideChange
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "Comment",
                    style = AppTheme.typography.textFieldTitle,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                )

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        state = state.commentRichText,
                    )
                    HorizontalDivider(color = Color(0xFFF6F6F6))
                    OutlinedRichTextEditor(
                        state = state.commentRichText,
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

                AiButton(
                    text = "Explain the front side",
                    onClick = { onAiGenerateClick(AiGenerate.MakeDefinition) },
                    isLoading = state.aiButtonLoading.isLoading(1)
                )
                Spacer(Modifier.height(7.dp))
                AiButton(
                    text = "Make some example from front side",
                    onClick = { onAiGenerateClick(AiGenerate.MakeExampleSentences) },
                    isLoading = state.aiButtonLoading.isLoading(0)
                )
                Spacer(Modifier.height(100.dp))
            }

            SecondaryBigButton(
                onClick = {
                    onAddCardButton()
                },
                modifier = Modifier
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
private fun AiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
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
            .clickable(onClick = onClick, enabled = !isLoading)
            .padding(vertical = 8.dp, horizontal = 14.dp)
    ) {
        if (isLoading) {
            Box(
                Modifier
                    .size(20.dp)
                    .padding(2.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFCA40D6),
                    strokeWidth = 2.dp
                )
            }
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_ai_model),
                contentDescription = null,
                tint = Color(0xFFCA40D6),
                modifier = Modifier.size(20.dp)
            )
        }
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
private fun RowScope.SideTextColumn(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    phonetic: String? = null
) {
    Column(modifier = Modifier.weight(0.5f)) {
        Text(
            text = title,
            style = AppTheme.typography.textFieldTitle,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(5.dp))
        var active by remember { mutableStateOf(false) }
        Box {
            BasicTextField(
                value = value,
                textStyle = AppTheme.typography.body2.copy(
                    fontFamily = DMSansVazir,
                    fontWeight = FontWeight.Bold
                ),
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
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                visualTransformation = nonEditableSuffixTransformation(
                    phonetic ?: ""
                ).takeIf { value.isNotBlank() } ?: VisualTransformation.None,
                decorationBox = {
                    it()
                    if (value.isEmpty()) {
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
}


/**
 * VisualTransformation that appends a non-editable, non-selectable suffix.
 * The caret/selection cannot move into the suffix.
 */
fun nonEditableSuffixTransformation(
    suffix: String,
    suffixStyle: SpanStyle = SpanStyle(color = Color.Gray)
): VisualTransformation = VisualTransformation { text ->
    val base = text.text
    val display = buildString {
        append(base)
        if (suffix.isNotEmpty()) {
            append(" ")
            append(suffix)
        }
    }

    // Build styled text: normal content + styled suffix
    val annotated = AnnotatedString.Builder(display).apply {
        if (suffix.isNotEmpty()) {
            addStyle(
                suffixStyle.copy(fontFamily = Charis, fontWeight = FontWeight.Normal),
                start = base.length + 1,
                end = base.length + suffix.length + 1
            )
        }
    }.toAnnotatedString()

    // Map original offsets <-> transformed offsets, clamping anything in the suffix
    val mapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            // original length maps to just before suffix
            val clamped = offset.coerceIn(0, base.length)
            return clamped // positions 0..base.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            // any position in the suffix maps back to the last original index
            return offset.coerceIn(0, base.length)
        }
    }

    TransformedText(annotated, mapping)
}