package io.kayt.refluent.feature.home

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.kayt.refluent.core.ui.component.LargeTopmostAppBar
import io.kayt.refluent.core.ui.component.MeshGradient
import io.kayt.refluent.core.ui.component.TopmostAppBarAnimationTimeline
import io.kayt.refluent.core.ui.component.TopmostAppBarContentScrollBehaviour
import io.kayt.refluent.core.ui.component.TopmostAppBarDividerPosition
import io.kayt.refluent.core.ui.component.animateOffsetOnBorder
import io.kayt.refluent.core.ui.component.rememberTopmostAppBarState
import io.kayt.refluent.core.ui.component.topmostAppBarAnimatableProperties
import io.kayt.refluent.core.ui.theme.AppTheme
import io.kayt.refluent.feature.home.component.DeckCard
import io.kayt.refluent.feature.home.component.SearchTextFiled
import androidx.compose.ui.util.lerp

@Composable
internal fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    HomeScreen(modifier = Modifier)
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {

    val backgroundMeshAnimation by animateOffsetOnBorder(50_000)
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
                LargeTopmostAppBar(
                    state = topmostAppBarState,
                    navigationIcon = {},
                    draggable = false,
                    contentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Fixed,
                    animatableProperties = topmostAppBarAnimatableProperties(
                        defaultStart = TopmostAppBarAnimationTimeline.Scrolled,
                    ) {
                        titleAlpha at TopmostAppBarAnimationTimeline.Collapsed with tween(durationMillis = 210)
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
                            .alpha(lerp(1f,0f, EaseOutExpo.transform(fraction)))
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
            },
            modifier = Modifier.nestedScroll(topmostAppBarState.nestedScrollConnection)
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(top = innerPadding.calculateTopPadding() - 20.dp)
                    .padding(horizontal = 17.dp)
            ) {
                Box {
                    val lazyListState = rememberLazyListState()
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(
                            top = 40.dp,
                            bottom = innerPadding.calculateBottomPadding()
                        )
                    ) {
                        repeat(10) {
                            item {
                                DeckCard(
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = {},
                                    onStudyClick = {}
                                )
                            }
                        }
                    }
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .blur(10.dp)
//                            .height(30.dp)
//                            .alpha(if (lazyListState.canScrollBackward) 1f else 0f)
//                            .background(
//                                Brush.verticalGradient(
//                                    0.0f to Color.White,
//                                    1.0f to Color.Transparent
//                                )
//                            )
//                    )
                }
            }
        }
    }
}