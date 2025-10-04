package io.kayt.refluent.feature.home.adddeck

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.DeleteAlertDialog
import io.kayt.refluent.core.ui.component.button.DeleteButton
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.floor

internal const val COLOR_FIXED: Int = 0xFFEFE3B1.toInt()

@Composable
fun AddDeckModal(
    isEditing: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    viewModel: AddDeckViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                AddDeckEvent.DeckDeletedSuccessfully,
                AddDeckEvent.DeckAddedSuccessfully -> onBackClick()
            }
        }
    }
    AddDeckModal(
        state = state,
        isEditing = isEditing,
        index = index,
        onNameChanges = viewModel::onNameChanges,
        onAddClick = viewModel::addNewDeck,
        onDeleteClick = viewModel::delete,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class, FlowPreview::class)
@Composable
private fun AddDeckModal(
    state: AddDeckUiState,
    isEditing: Boolean,
    index: Int,
    onDeleteClick: () -> Unit,
    onNameChanges: (String) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = remember { mutableFloatStateOf(((index % 10f) / 10).coerceAtMost(1f)) }
    val currentState by rememberUpdatedState(state)
    LaunchedEffect(Unit) {
        snapshotFlow { fraction.floatValue }.collectLatest {
            currentState.color = steppedRainbowColor(it).toArgb()
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(38.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(state.color),
                        Color(COLOR_FIXED)
                    )
                )
            )
            .scrollableHorizontalFraction(fraction)
    ) {
        val placeholderBaseColor = Color.Black.copy(alpha = 0.4f)
        val placeholderAnimatable = remember { Animatable(placeholderBaseColor) }
        Box {
            Column {
                Column(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        if (isEditing) "Edit the deck"
                        else "Create new deck",
                        style = AppTheme.typography.body2,
                        color = Color(0x66000000)
                    )
                    Spacer(Modifier.height(16.dp))
                    BasicTextField(
                        value = state.name.uppercase(),
                        onValueChange = { onNameChanges(it) },
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        textStyle = AppTheme.typography.headline1,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                if (state.name.isEmpty()) {
                                    Text(
                                        text = "ENTER THE NAME OF THE DECK",
                                        color = placeholderAnimatable.value,
                                        style = AppTheme.typography.headline1
                                    )
                                }
                                innerTextField() // <-- actual editable text goes here
                            }
                        }
                    )
                    Spacer(Modifier.height(30.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Scroll horizontally to change the color",
                            style = AppTheme.typography.body2,
                            color = Color(0x66000000)
                        )
                        Image(
                            painter = painterResource(R.drawable.scroll_horizontally),
                            null,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .width(62.dp)
                        )

                    }
                }
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(23.dp))
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (!isEditing) {
                        Text(
                            "A deck is a collection of flashcards grouped together on a specific topic. " +
                                    "Think of it like a folder for your study cards. \n",
                            style = AppTheme.typography.body2,
                            color = Color(0xFF929292),
                            modifier = Modifier
                                .padding(horizontal = 23.dp)
                                .padding(top = 23.dp)
                        )
                    } else {
                        var deleteConfirmationDialogVisible by remember { mutableStateOf(false) }
                        DeleteButton(
                            {
                                deleteConfirmationDialogVisible = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .padding(bottom = 11.dp)
                                .padding(horizontal = 23.dp)
                        ) {
                            Text("Delete Deck")
                        }
                        if (deleteConfirmationDialogVisible) {
                            DeleteAlertDialog(
                                onDeleteClick = {
                                    deleteConfirmationDialogVisible = false
                                    onDeleteClick()
                                },
                                onDismissRequest = { deleteConfirmationDialogVisible = false }
                            )
                        }
                    }
                    val scope = rememberCoroutineScope()
                    PrimaryButton(
                        onClick = {
                            if (state.name.isNotBlank()) {
                                onAddClick()
                            } else {
                                scope.launch {
                                    placeholderAnimatable.animateTo(
                                        Color.Red,
                                        tween(140, easing = FastOutSlowInEasing)
                                    )
                                    placeholderAnimatable.animateTo(
                                        placeholderBaseColor,
                                        tween(140, easing = FastOutSlowInEasing)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .padding(bottom = 23.dp)
                            .padding(horizontal = 23.dp)
                    ) {
                        Text(
                            if (isEditing)
                                "Save Deck"
                            else
                                "Create Deck"
                        )
                    }
                    LaunchedEffect(WindowInsets.isImeVisible) {
                        scrollState.animateScrollBy(1000f)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .background(
                        Color.White.copy(0.4f),
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_close_general),
                    null,
                    tint = Color(0xFF645315)
                )
            }
        }
    }
}


@SuppressLint("ComposeModifierComposed")
fun Modifier.scrollableHorizontalFraction(
    fractionState: MutableState<Float>,
    enabled: Boolean = true,
    rightIncreases: Boolean = true
): Modifier = composed {
    var widthPx by remember { mutableIntStateOf(0) }
    var accumulatedPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(widthPx) {
        if (widthPx > 0) {
            accumulatedPx = (fractionState.value * widthPx).coerceIn(0f, widthPx.toFloat())
        }
    }

    LaunchedEffect(fractionState.value, widthPx) {
        if (widthPx > 0) {
            val target = (fractionState.value * widthPx).coerceIn(0f, widthPx.toFloat())
            if ((target - accumulatedPx).let { kotlin.math.abs(it) > 0.5f }) {
                accumulatedPx = target
            }
        }
    }

    val scrollableState = rememberScrollableState { deltaPx ->
        if (!enabled || widthPx <= 0) return@rememberScrollableState 0f
        val signed = if (rightIncreases) -deltaPx else deltaPx
        accumulatedPx = (accumulatedPx + signed).coerceIn(0f, widthPx.toFloat())

        val newFraction = (accumulatedPx / widthPx).coerceIn(0f, 1f)
        if (newFraction != fractionState.value) {
            fractionState.value = newFraction
        }

        // We fully consume; this is a virtual scroll surface.
        deltaPx
    }

    this
        .onSizeChanged {
            widthPx = it.width.coerceAtLeast(1)
        }
        .scrollable(
            state = scrollableState,
            orientation = Orientation.Horizontal,
            enabled = enabled
        )
}

fun steppedRainbowColor(
    fraction: Float,
    low: Int = 111,
    high: Int = 250
): Color {
    val f = ((fraction % 1f) + 1f) % 1f
    val span = (high - low).toFloat()

    val t = f * 6f
    val seg = floor(t).toInt()
    val p = t - seg

    val r: Int
    val g: Int
    val b: Int

    when (seg) {
        0 -> {
            r = high
            g = (low + span * p).toInt()
            b = low
        }

        1 -> {
            r = (high - span * p).toInt();
            g = high;
            b = low
        }

        2 -> {
            r = low
            g = high
            b = (low + span * p).toInt()
        }

        3 -> {
            r = low
            g = (high - span * p).toInt()
            b = high
        }

        4 -> {
            r = (low + span * p).toInt()
            g = low
            b = high
        }

        else -> {
            r = high
            g = low
            b = (high - span * p).toInt()
        }
    }

    return Color(
        red = r.coerceIn(0, 255),
        green = g.coerceIn(0, 255),
        blue = b.coerceIn(0, 255),
    )
}

@Preview
@Composable
private fun AddDeckModalPreview() {
    AddDeckModal(isEditing = false, {})
}