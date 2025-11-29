package io.kayt.refluent.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kayt.refluent.core.ui.R
import io.kayt.refluent.core.ui.component.DeckEntry
import io.kayt.refluent.core.ui.component.LocalSharedTransitionScope
import io.kayt.refluent.core.ui.component.MeshGradient
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.core.ui.theme.typography.LifeSaver
import io.kayt.refluent.feature.home.component.DeckCard
import io.kayt.refluent.feature.home.component.SearchTextFiled
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
import java.time.LocalTime

@Composable
internal fun HomeScreen(
    onAddDeckClick: (deckCount: Int) -> Unit,
    onDeckClick: (Long) -> Unit,
    onDeckEditClick: (Long) -> Unit,
    onStudyClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    BackHandler(query.isNotEmpty()) {
        viewModel.onQueryChange("")
        focusManager.clearFocus()
        keyboardManager?.hide()
    }
    HomeScreen(
        state = state,
        query = query,
        onAddDeckClick = {
            val deckCount = ((state as? HomeUiState.Success)?.decks?.size) ?: 0
            onAddDeckClick(deckCount)
        },
        onDeckClick = onDeckClick,
        onDeckEditClick = onDeckEditClick,
        onQueryChange = viewModel::onQueryChange,
        searchResult = searchResult,
        onStudyClick = onStudyClick
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    query: String,
    searchResult: SearchResult,
    onAddDeckClick: () -> Unit,
    onStudyClick: (Long) -> Unit,
    onDeckEditClick: (Long) -> Unit,
    onDeckClick: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
) {
    with(LocalSharedTransitionScope.current) {
        MeshGradient(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEFEFE)),
            mesh = MeshGradient(
                width = 3,
                height = 5,
                points = {
                    point(0f, 0f, Color(0x4AFFE292))
                }
            )
        ) {
            val topmostAppBarState = rememberTopmostAppBarState()
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Refluent",
                            style = AppTheme.typography.greeting1,
                            modifier = Modifier.padding(horizontal = 30.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            {},
                            modifier = Modifier.padding(horizontal = 15.dp)
                        ) {
                            Box {
                                Icon(
                                    painter = painterResource(R.drawable.share_desktop),
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = "Open in Browser"
                                )
                                // if connection is established and online
                                if (false) {
                                    Box(
                                        Modifier
                                            .offset(y = -2.dp, x = 2.dp)
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3B9E20))
                                            .align(
                                                Alignment.TopEnd
                                            )
                                    )
                                }
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    Column(
                        Modifier
                            .padding(top = 30.dp)
                            .padding(horizontal = 17.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (searchResult is SearchResult.NoSearch) {
                                AnimatedContent(
                                    targetState = state,
                                    modifier = Modifier.fillMaxSize(),
                                    contentKey = { it::class }) { state ->
                                    when (state) {
                                        is HomeUiState.Success -> {
                                            val lazyListState = rememberLazyListState()
                                            LazyColumn(
                                                state = lazyListState,
                                                contentPadding = PaddingValues(
                                                    top = 40.dp,
                                                    bottom = innerPadding.calculateBottomPadding()
                                                )
                                            ) {
                                                val decks = state.decks
                                                items(decks.size) { index ->
                                                    DeckCard(
                                                        deck = decks[index],
                                                        modifier = Modifier.padding(bottom = 10.dp),
                                                        onClick = { onDeckClick(decks[index].id) },
                                                        onStudyClick = { onStudyClick(decks[index].id) },
                                                        onLongPress = {
                                                            onDeckEditClick(decks[index].id)
                                                        },
                                                        deckId = decks[index].id
                                                    )
                                                }
                                                item {
                                                    Box(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 10.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        TextButton(
                                                            onAddDeckClick,
                                                            modifier = Modifier
                                                                .height(60.dp)
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 30.dp)
                                                        ) {
                                                            Text(
                                                                text = "Create new deck",
                                                                style = AppTheme.typography.body1.copy(
                                                                    fontWeight = FontWeight.Medium
                                                                ),
                                                                color = Color(0xFF222222)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        is HomeUiState.Empty -> {
                                            Box {
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .padding(
                                                            PaddingValues(bottom = innerPadding.calculateBottomPadding())
                                                        ),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Spacer(Modifier.weight(1f))
                                                    Box(
                                                        modifier = Modifier.offset(
                                                            x = 30.dp,
                                                            y = 0.dp
                                                        )
                                                    ) {
                                                        Spacer(
                                                            Modifier
                                                                .offset(x = -(193 / 2).dp, y = 0.dp)
                                                                .size(193.dp)
                                                                .background(
                                                                    Color(0xFFFFF9D4),
                                                                    CircleShape
                                                                )
                                                        )
                                                        Image(
                                                            painter = painterResource(R.drawable.first_ever_brainy),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(137.dp, 185.dp)
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier.weight(1.4f),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            "No Decks Found",
                                                            fontFamily = LifeSaver,
                                                            fontSize = 32.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            lineHeight = 55.sp,
                                                            modifier = Modifier.padding(top = 30.dp)
                                                        )
                                                    }
                                                    PrimaryButton(
                                                        onAddDeckClick,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 20.dp)
                                                            .height(71.dp)
                                                    ) {
                                                        Text("Create new deck")
                                                    }
                                                    Spacer(Modifier.height(50.dp))
                                                }
                                            }
                                        }

                                        HomeUiState.Loading -> {}
                                    }
                                }
                            } else if (searchResult is SearchResult.SearchContent) {
                                if (searchResult.cards.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier.padding(top = 20.dp)
                                    ) {
                                        val selectedIndex = remember { mutableIntStateOf(-1) }
                                        val scrollState = rememberLazyListState()
                                        LaunchedEffect(Unit) {
                                            merge(
                                                snapshotFlow { scrollState.isScrollInProgress },
                                                snapshotFlow { selectedIndex.intValue }.debounce(
                                                    2_500
                                                )
                                            )
                                                .collectLatest {
                                                    selectedIndex.intValue = -1
                                                }
                                        }
                                        LazyColumn(
                                            state = scrollState,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(searchResult.cards.size) {
                                                val card = searchResult.cards[it]
                                                Box {
                                                    Row {
                                                        DeckEntry(
                                                            card = card.card,
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .fillMaxWidth()
                                                        )
                                                        Spacer(Modifier.width(30.dp))
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .height(40.dp)
                                                            .widthIn(min = 40.dp)
                                                            .clip(CircleShape)
                                                            .clickable(
                                                                onClick = {
                                                                    if (selectedIndex.intValue == it) {
                                                                        onDeckClick(card.card.deckId)
                                                                    } else {
                                                                        selectedIndex.intValue = it
                                                                    }
                                                                },
                                                                indication = null,
                                                                interactionSource = null
                                                            )
                                                            .background(
                                                                Brush.horizontalGradient(
                                                                    listOf(
                                                                        Color(card.deckColor.first),
                                                                        Color(card.deckColor.second)
                                                                    )
                                                                )
                                                            )
                                                            .padding(
                                                                horizontal = 15.dp,
                                                                vertical = 5.dp
                                                            )
                                                            .animateContentSize()
                                                            .align(Alignment.CenterEnd),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (selectedIndex.intValue == it) {
                                                            Text(
                                                                text = card.deckName.uppercase(),
                                                                style = AppTheme.typography.body2.copy(
                                                                    fontWeight = FontWeight.Bold
                                                                ),
                                                                modifier = Modifier.padding(end = 30.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(horizontal = 10.dp),
                                                    color = Color(0xFFEFEFEF)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .imePadding(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No cards found",
                                            style = AppTheme.typography.headline2
                                        )
                                    }
                                }
                            }
                        }
                    }
                    SearchTextFiled(
                        value = query,
                        onValueChange = { onQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

private fun getGreeting(): String {
    val now = LocalTime.now()
    return when (now.hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..21 -> "Good evening"
        else -> "Good night"
    }
}