package io.kayt.refluent.feature.home

import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kayt.refluent.core.ui.component.LargeTopmostAppBar
import io.kayt.refluent.core.ui.component.MeshGradient
import io.kayt.refluent.core.ui.component.TopmostAppBarAnimationTimeline
import io.kayt.refluent.core.ui.component.TopmostAppBarContentScrollBehaviour
import io.kayt.refluent.core.ui.component.TopmostAppBarDividerPosition
import io.kayt.refluent.core.ui.component.button.PrimaryButton
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.component.topmostAppBarAnimatableProperties
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.feature.home.component.DeckCard
import io.kayt.refluent.feature.home.component.SearchTextFiled

@Composable
internal fun HomeScreen(
    onAddDeckClick: () -> Unit,
    onDeckClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onAddDeckClick = onAddDeckClick,
        onDeckClick = onDeckClick,
        modifier = Modifier
    )
}

@Composable
private fun HomeScreen(
    state: HomeUiState,
    onAddDeckClick: () -> Unit,
    onDeckClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
                if (state is HomeUiState.Success) {
                    LargeTopmostAppBar(
                        state = topmostAppBarState,
                        navigationIcon = {},
                        draggable = false,
                        contentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Fixed,
                        animatableProperties = topmostAppBarAnimatableProperties(
                            defaultStart = TopmostAppBarAnimationTimeline.Scrolled,
                        ) {
                            titleAlpha at TopmostAppBarAnimationTimeline.Collapsed with tween(
                                durationMillis = 210
                            )
                            backgroundAlpha at TopmostAppBarAnimationTimeline.Never
                            dividerAlpha at TopmostAppBarAnimationTimeline.Never
                        },
                        dividerPosition = TopmostAppBarDividerPosition.Bottom,
                        title = {
                            Text(
                                "Refluent",
                                style = AppTheme.typography.headline1.copy(fontSize = 20.sp)
                            )
                        },
                        sticky = { _, collapsedFraction, _ ->
                            Spacer(modifier = Modifier.height(lerp(20.dp, 0.dp, collapsedFraction)))
                            SearchTextFiled(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    ) { padding, fraction ->
                        Column(
                            modifier = Modifier
                                .alpha(lerp(1f, 0f, EaseOutExpo.transform(fraction)))
                                .padding(padding.windowPadding)
                                .padding(horizontal = 16.dp)
                                .padding(top = 26.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 9.dp)
                        ) {
                            Text(
                                "Good Morning,",
                                style = AppTheme.typography.greeting1
                            )
                            Text(
                                "Parisa",
                                style = AppTheme.typography.greeting2
                            )
                        }
                    }
                }
            },
            modifier = Modifier.nestedScroll(topmostAppBarState.nestedScrollConnection)
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(top = innerPadding.calculateTopPadding() - 20.dp)
                    .padding(horizontal = 17.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state is HomeUiState.Success) {
                        val lazyListState = rememberLazyListState()
                        LazyColumn(
                            state = lazyListState,
                            contentPadding = PaddingValues(
                                top = 40.dp,
                                bottom = innerPadding.calculateBottomPadding()
                            )
                        ) {
                            val decks = state.decks
                            items(decks.size) {
                                DeckCard(
                                    deck = decks[it],
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = { onDeckClick(0) },
                                    onStudyClick = {}
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No Decks Found",
                                style = AppTheme.typography.body1,
                                modifier = Modifier.padding(top = 100.dp)
                            )
                            Spacer(Modifier.height(20.dp))
                            PrimaryButton(
                                onAddDeckClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Text("Create your first deck")
                            }
                        }
                    }
                }
            }
        }
    }
}