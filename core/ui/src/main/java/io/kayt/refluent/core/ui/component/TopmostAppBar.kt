package io.kayt.refluent.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import io.kayt.refluent.core.ui.theme.AppTheme
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val TOPMOST_DIVIDER_HEIGHT_DP = 2

/**
 * TopmostAppBar is the simplest TopmostAppBar that shows a title in center,
 * container and title color can be set so it is possible to animate them on consumer site
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopmostAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = TopmostAppBarDefaults.containerColor,
    contentColor: Color = TopmostAppBarDefaults.titleContentColor,
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                ProvideTextStyle(
                    TopmostAppBarDefaults.textStyle
                ) { title?.let { Text(it.uppercase()) } }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            expandedHeight = TopmostAppBarDefaults.height,
            modifier = modifier,
        )
        HorizontalDivider(
            thickness = TOPMOST_DIVIDER_HEIGHT_DP.dp,
            color = AppTheme.colors.backgroundGrey.copy(alpha = contentColor.alpha)
        )
    }
}

// /**
// * MediumTopmostAppBar is just for root screens that doesn't have any navigation buttons,
// * in MediumTopmostAppBar, title has an animation for transition from expanded to collapsed.
// */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumTopmostAppBar(
    state: TopmostAppBarState,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) = FreeformTopmostAppBar(
    state = state,
    navigationIcon = {},
    title = title,
    contentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Scroll,
    animatableProperties = topmostAppBarAnimatableProperties(
        defaultStart = TopmostAppBarAnimationTimeline.Scrolled
    ) {
        dividerAlpha at TopmostAppBarAnimationTimeline.Collapsed
    },
    modifier = modifier,
    draggable = false,
    snapInDraggableArea = true,
    actions = actions,
) { paddings, fraction ->
    val bottomTitleAlpha = 1f - fraction
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            // 1.dp is added because the height should be more that the collapsed height otherwise it doesn't work
            .height(paddings.topAppbarPadding.calculateTopPadding() + 1.dp)
            .fillMaxWidth()
            .graphicsLayer(alpha = bottomTitleAlpha)
            .background(TopmostAppBarDefaults.containerColor)
            .padding(horizontal = TopmostAppBarDefaults.topAppBarTitleInset)
            .padding(paddings.windowPadding)
    ) {
        val mergedStyle =
            LocalTextStyle.current.merge(TopmostAppBarDefaults.Medium.textStyle)
        CompositionLocalProvider(
            LocalContentColor provides TopmostAppBarDefaults.titleContentColor,
            LocalTextStyle provides mergedStyle,
            content = title
        )
    }
}

/**
 * This MediumTopmostAppBar can have a navigation icon, the title comes to the next line, so this
 * app bar has two title that use in Expanded and Collapsed mode.
 *
 * @param title composable can be in any height, there is no limit
 * @param smallTitle is used in collapsed mode, there is a limit of [TopmostAppBarDefaults.height]
 *  for height, if nothing passed as smallTitle, the [title] will be used
 */
@Composable
fun MediumTopmostAppBar(
    state: TopmostAppBarState,
    navigationIcon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    smallTitle: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) = FreeformTopmostAppBar(
    modifier = modifier,
    state = state,
    navigationIcon = navigationIcon,
    title = smallTitle ?: title,
    contentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Scroll,
    animatableProperties = topmostAppBarAnimatableProperties {
        titleAlpha animateProgress TopTitleAlphaEasing::transform
        dividerAlpha at TopmostAppBarAnimationTimeline.Collapsed with spring()
    },
    draggable = false,
    actions = actions,
) { paddings, fraction ->
    val bottomTitleAlpha = 1f - fraction
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = bottomTitleAlpha)
            .background(TopmostAppBarDefaults.containerColor)
            .padding(horizontal = TopmostAppBarDefaults.topAppBarTitleInset)
            .padding(paddings.topAppbarPadding)
    ) {
        val mergedStyle =
            LocalTextStyle.current.merge(TopmostAppBarDefaults.Medium.textStyle)
        CompositionLocalProvider(
            LocalContentColor provides TopmostAppBarDefaults.titleContentColor,
            LocalTextStyle provides mergedStyle,
            content = title
        )
    }
}

/**
 * Some of screens need a large TopAppBar that at the beginning be transparent and become white in
 * when collapsed completely.
 *
 * @sample LargeTopmostAppBarPreview
 *
 * @param content a composable lambda on behind of everything
 * @param animatableProperties define the animation for each properties such as title, background and divider,
 *  the animation can be both TimeBased and ProgressBased, use [topmostAppBarAnimatableProperties]
 * @param contentScrollBehaviour specify how the content should react to the scroll
 * @param dividerPosition let consumers to decide where the divider should be, under the title
 *  or under the large content
 */
@Composable
fun LargeTopmostAppBar(
    state: TopmostAppBarState,
    navigationIcon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentScrollBehaviour: TopmostAppBarContentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Scroll,
    animatableProperties: TopmostAppBarAnimatableProperties = topmostAppBarAnimatableProperties(
        defaultStart = TopmostAppBarAnimationTimeline.Collapsed
    ),
    dividerPosition: TopmostAppBarDividerPosition = TopmostAppBarDividerPosition.Top,
    draggable: Boolean = true,
    snapInDraggableArea: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    sticky: @Composable (
        paddings: TopmostAppBarPaddingValues,
        collapsedFraction: Float,
        contentOffset: Float
    ) -> Unit = { _, _, _ -> },
    content: @Composable (paddings: TopmostAppBarPaddingValues, collapsedFraction: Float) -> Unit,
) = FreeformTopmostAppBar(
    state = state,
    navigationIcon = navigationIcon,
    title = title,
    contentScrollBehaviour = contentScrollBehaviour,
    animatableProperties = animatableProperties,
    dividerPosition = dividerPosition,
    modifier = modifier,
    draggable = draggable,
    snapInDraggableArea = snapInDraggableArea,
    actions = actions,
    sticky = sticky,
    content = content,
)

/**
 * if none of the built-in TopmostAppBar is not suitable for some screens, we can combine the simplest one
 * which is [TopmostAppBar] with this HeadlessTopmostAppBar in the [content] composable lambda
 * and write some logic to fulfill the requirements.
 */
@Composable
fun HeadlessTopmostAppBar(
    state: TopmostAppBarState,
    modifier: Modifier = Modifier,
    contentScrollBehaviour: TopmostAppBarContentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Fixed,
    draggable: Boolean = true,
    snapInDraggableArea: Boolean = true,
    minHeight: Dp = Dp.Unspecified,
    content: @Composable (paddings: TopmostAppBarPaddingValues, collapsedFraction: Float) -> Unit,
) = FreeformTopmostAppBar(
    state = state,
    navigationIcon = {},
    title = { },
    contentScrollBehaviour = contentScrollBehaviour,
    animatableProperties = topmostAppBarAnimatableProperties(
        defaultStart = TopmostAppBarAnimationTimeline.Never
    ),
    modifier = modifier,
    draggable = draggable,
    snapInDraggableArea = snapInDraggableArea,
    actions = {},
    headless = true,
    content = content,
    requestedMinHeight = minHeight
)

data class TopmostAppBarPaddingValues(
    /**
     * This property gives the top window inset (normally status bar height) as padding
     */
    val windowPadding: PaddingValues,

    /**
     * This inset is the result of (top window inset + appbar height), so it can be used directly
     * if the composable is supposed to start from the bottom of top appbar
     */
    val topAppbarPadding: PaddingValues,
)

enum class TopmostAppBarDividerPosition {
    /** Under the title */
    Top,

    /** Under the content */
    Bottom
}

@Suppress("MaxLineLength")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FreeformTopmostAppBar(
    state: TopmostAppBarState,
    navigationIcon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    contentScrollBehaviour: TopmostAppBarContentScrollBehaviour,
    animatableProperties: TopmostAppBarAnimatableProperties,
    actions: @Composable RowScope.() -> Unit,
    draggable: Boolean,
    modifier: Modifier = Modifier,
    headless: Boolean = false,
    dividerPosition: TopmostAppBarDividerPosition = TopmostAppBarDividerPosition.Bottom,
    snapInDraggableArea: Boolean = true,
    sticky: @Composable (
        paddings: TopmostAppBarPaddingValues,
        collapsedFraction: Float,
        contentOffset: Float
    ) -> Unit = { _, _, _ -> },
    requestedMinHeight: Dp = Dp.Unspecified,
    content: @Composable (paddings: TopmostAppBarPaddingValues, collapsedFraction: Float) -> Unit,
) {
    val ignoreMinHeight = requestedMinHeight != Dp.Unspecified
    val appBarState = state.topBarState
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
    val hideTopRowSemantics by remember {
        derivedStateOf {
            appBarState.collapsedFraction < 0.5f
        }
    }

    // Animations
    val titleAlpha by animatableProperties.titleAlpha.animate(appBarState)
    val containerColorAlpha by animatableProperties.backgroundAlpha.animate(appBarState)
    val dividerAlpha by animatableProperties.dividerAlpha.animate(appBarState)

    // Draggable is the ability to scroll on TopAppBar itself
    val appBarDragModifier = if (draggable) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                if (state.canScroll()) {
                    appBarState.heightOffset += delta
                }
            },
            onDragStopped = { velocity ->
                if (snapInDraggableArea && state.canScroll()) {
                    state.settleAppBar(velocity)
                }
            }
        )
    } else {
        Modifier
    }

    val windowTopInset = TopmostAppBarDefaults.windowInsets.only(
        WindowInsetsSides.Top
    ).asPaddingValues().calculateTopPadding()

    val minimumHeight = if (headless) 0.dp else TopmostAppBarDefaults.height
    val divider = @Composable {
        if (animatableProperties.dividerAlpha.timeline.notNever()) {
            HorizontalDivider(
                thickness = TOPMOST_DIVIDER_HEIGHT_DP.dp,
                color = AppTheme.colors.backgroundGrey.copy(alpha = dividerAlpha)
            )
        }
    }
    Column(modifier = modifier.then(appBarDragModifier)) {
        Box {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        val childPlaceable =
                            measurable.measure(
                                constraints.copy(
                                    minHeight =
                                    if (ignoreMinHeight) {
                                        requestedMinHeight.roundToPx()
                                    } else {
                                        TopmostAppBarDefaults.height.roundToPx() + windowTopInset.roundToPx()
                                    }
                                )
                            )
                        // update the offset whenever the layout updated
                        appBarState.heightOffsetLimit = min(
                            0f,
                            (childPlaceable.height.toFloat() - minimumHeight.roundToPx() - (if (ignoreMinHeight) requestedMinHeight.roundToPx() else windowTopInset.roundToPx())).unaryMinus()
                        )

                        val scrolledOffsetValue = appBarState.heightOffset
                        val heightOffset =
                            if (scrolledOffsetValue.isNaN()) 0 else scrolledOffsetValue.roundToInt()
                        val layoutHeight = max(0, childPlaceable.height + heightOffset)
                        layout(constraints.maxWidth, layoutHeight) {
                            if (layoutHeight != 0) {
                                childPlaceable.place(
                                    0,
                                    when (contentScrollBehaviour) {
                                        TopmostAppBarContentScrollBehaviour.Parallax -> (layoutHeight - childPlaceable.height) / 2
                                        TopmostAppBarContentScrollBehaviour.Fixed -> 0
                                        TopmostAppBarContentScrollBehaviour.Scroll -> layoutHeight - childPlaceable.height
                                    }
                                )
                            }
                        }
                    },
                content = {
                    val windowPadding = TopmostAppBarDefaults.windowInsets.asPaddingValues()
                    content(
                        remember {
                            TopmostAppBarPaddingValues(
                                windowPadding = windowPadding,
                                topAppbarPadding = if (ignoreMinHeight) {
                                    PaddingValues(top = requestedMinHeight)
                                } else {
                                    PaddingValues(top = windowTopInset + TopmostAppBarDefaults.height)
                                }
                            )
                        },
                        appBarState.collapsedFraction
                    )
                }
            )
            if (!headless) {
                Column {
                    TopmostAppBarLayout(
                        modifier =
                        Modifier
                            .background(TopmostAppBarDefaults.containerColor.copy(alpha = containerColorAlpha))
                            .windowInsetsPadding(TopmostAppBarDefaults.windowInsets)
                            // clip after padding so we don't show the title over the inset area
                            .clipToBounds()
                            .heightIn(max = TopmostAppBarDefaults.height),
                        scrolledOffset = { 0f },
                        navigationIconContentColor = TopmostAppBarDefaults.navigationIconContentColor,
                        titleContentColor = TopmostAppBarDefaults.titleContentColor,
                        actionIconContentColor = TopmostAppBarDefaults.actionIconContentColor,
                        title = title,
                        titleTextStyle = TopmostAppBarDefaults.textStyle,
                        titleAlpha = titleAlpha,
                        titleBiasAlignment = {
                            BiasAlignment(
                                horizontalBias = 0f,
                                verticalBias = 0f
                            )
                        },
                        hideTitleSemantics = hideTopRowSemantics,
                        navigationIcon = navigationIcon,
                        actions = actionsRow,
                    )
                    if (dividerPosition == TopmostAppBarDividerPosition.Top) {
                        divider()
                    }
                }
            }
        }
        val windowPadding = TopmostAppBarDefaults.windowInsets.asPaddingValues()
        sticky(
            remember {
                TopmostAppBarPaddingValues(
                    windowPadding = windowPadding,
                    topAppbarPadding = PaddingValues(
                        top = windowTopInset + TopmostAppBarDefaults.height
                    )
                )
            },
            appBarState.collapsedFraction,
            appBarState.contentOffset
        )

        if (dividerPosition == TopmostAppBarDividerPosition.Bottom) {
            divider()
        }
    }
}

/**
 * this is the only way to create an object of TopmostAppBarState,
 *
 * if you need to have the animation only if the list is bigger than the screen, you can use `canScroll`
 * and check your scroll state for example `{ scrollState.canScrollForward }`
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTopmostAppBarState(
    canScroll: () -> Boolean = { true },
    snap: Boolean = true,
): TopmostAppBarState {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topBarState,
        canScroll = canScroll,
        snapAnimationSpec = if (snap) remember { tween(150) } else null
    )
    return remember {
        TopmostAppBarState(
            topBarState,
            scrollBehavior,
            canScroll
        )
    }.apply { this.canScroll = canScroll }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class TopmostAppBarState internal constructor(
    internal val topBarState: TopAppBarState,
    private val scrollBehavior: TopAppBarScrollBehavior,
    internal var canScroll: () -> Boolean,
) {
    val nestedScrollConnection =
        object : NestedScrollConnection by scrollBehavior.nestedScrollConnection {}

    suspend fun settleAppBar(available: Float) {
        // this function is used to call the private function inside AppBar compose
        scrollBehavior.nestedScrollConnection.onPostFling(
            Velocity.Zero,
            Velocity(0f, available)
        )
    }

    suspend fun expandSmoothly() {
        AnimationState(initialValue = topBarState.heightOffset).animateTo(
            targetValue = 0f,
            animationSpec = spring()
        ) {
            topBarState.heightOffset = value
        }
    }
}

class TopmostAppBarAnimatableProperties(
    val backgroundAlpha: TopmostAppBarAnimatableProperty,
    val titleAlpha: TopmostAppBarAnimatableProperty,
    val dividerAlpha: TopmostAppBarAnimatableProperty,
)

@Stable
class TopmostAppBarAnimatableProperty(
    val animation: TopmostAppBarElementAnimation,
    val timeline: TopmostAppBarAnimationTimeline,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun animate(
        state: TopAppBarState,
    ): State<Float> {
        when (animation) {
            is TopmostAppBarElementAnimation.ProgressBased -> {
                val animationState =
                    remember { mutableFloatStateOf(animation.transformer(state.collapsedFraction)) }
                animationState.floatValue = animation.transformer(state.collapsedFraction)
                return animationState
            }

            is TopmostAppBarElementAnimation.TimeBased -> {
                val startTime by timeline.rememberAnimationStartTime(state)
                val animatable = remember { Animatable(startTime.toFloat()) }
                LaunchedEffect(startTime) {
                    animatable.animateTo(startTime.toFloat(), animation.spec)
                }
                return animatable.asState()
            }
        }
    }
}

sealed interface TopmostAppBarElementAnimation {
    class TimeBased(val spec: AnimationSpec<Float>) : TopmostAppBarElementAnimation
    class ProgressBased(val transformer: (Float) -> Float) : TopmostAppBarElementAnimation
}

enum class TopmostAppBarAnimationTimeline {
    Beginning, Scrolled, Collapsed, ScrolledAfterCollapsed, Never;

    fun notNever() = this != Never

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun rememberAnimationStartTime(state: TopAppBarState): State<Boolean> {
        val isItTimeState = remember { mutableStateOf(false) }
        when (this) {
            Beginning -> isItTimeState.value = true
            Scrolled -> {
                val offsetNonZero by remember {
                    derivedStateOf {
                        state.collapsedFraction != 0f
                    }
                }
                isItTimeState.value = offsetNonZero
            }

            Collapsed -> {
                val collapsed by remember {
                    derivedStateOf {
                        state.collapsedFraction == 1f
                    }
                }
                isItTimeState.value = collapsed
            }

            ScrolledAfterCollapsed -> {
                val collapsed by remember {
                    derivedStateOf {
                        state.contentOffset != 0f
                    }
                }
                isItTimeState.value = collapsed
            }

            Never -> isItTimeState.value = false
        }
        return isItTimeState
    }
}

enum class TopmostAppBarContentScrollBehaviour {
    Parallax, Fixed, Scroll
}

internal object TopmostAppBarDefaults {
    val height: Dp = 46.dp
    val horizontalPadding = 4.dp
    val topAppBarTitleInset = 23.dp - horizontalPadding
    val navigationIconContentColor
        @Composable
        get() = AppTheme.colors.navIconPrimary

    val titleContentColor
        @Composable
        get() = AppTheme.colors.textPrimary

    val actionIconContentColor
        @Composable
        get() = AppTheme.colors.navIconPrimary

    val containerColor
        @Composable
        get() = AppTheme.colors.background

    val textStyle: TextStyle
        @Composable
        get() = AppTheme.typography.headline3

    val windowInsets: WindowInsets
        @Composable
        get() =
            WindowInsets.systemBars.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
            )

    object Medium {
        val expandedWithNavIconHeight: Dp = 99.dp
        val expandedHeight: Dp = 58.dp

        val textStyle: TextStyle
            @Composable
            get() = AppTheme.typography.headline1
    }
}

private fun Boolean.toFloat() = if (this) 1f else 0f

// =================== IMPLEMENTATION ===================

/** A functional interface for providing an app-bar scroll offset. */
private fun interface ScrolledOffset {
    fun offset(): Float
}

@Composable
private fun TopmostAppBarLayout(
    scrolledOffset: ScrolledOffset,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleAlpha: Float,
    titleBiasAlignment: () -> BiasAlignment,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopmostAppBarDefaults.horizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = TopmostAppBarDefaults.horizontalPadding)
                    .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics {} else Modifier)
                    .graphicsLayer(alpha = titleAlpha)
            ) {
                val mergedStyle = LocalTextStyle.current.merge(titleTextStyle)
                CompositionLocalProvider(
                    LocalContentColor provides titleContentColor,
                    LocalTextStyle provides mergedStyle,
                    content = title
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopmostAppBarDefaults.horizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions
                )
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

        val maxTitleWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }
        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        // Subtract the scrolledOffset from the maxHeight. The scrolledOffset is expected to be
        // equal or smaller than zero.
        val scrolledOffsetValue = scrolledOffset.offset()
        val heightOffset = if (scrolledOffsetValue.isNaN()) 0 else scrolledOffsetValue.roundToInt()

        val layoutHeight = if (constraints.maxHeight == Constraints.Infinity) {
            constraints.maxHeight
        } else {
            constraints.maxHeight + heightOffset
        }

        layout(constraints.maxWidth, layoutHeight) {
            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = (TopmostAppBarDefaults.height.roundToPx() - navigationIconPlaceable.height) / 2
            )

            // Title
            val titleY = titleBiasAlignment().verticalBias.let { bias ->
                val halfOfAppBar = layoutHeight / 2
                val baseY =
                    ((halfOfAppBar - titlePlaceable.height / 2) + halfOfAppBar * bias).roundToInt()
                min(baseY, layoutHeight - titlePlaceable.height)
            }
            titlePlaceable.placeRelative(
                x = titleBiasAlignment().horizontalBias.let { bias ->
                    val actionWidth =
                        if (titleY > actionIconsPlaceable.height && titleY > navigationIconPlaceable.height) {
                            0
                        } else {
                            actionIconsPlaceable.width
                        }
                    val navigationIconWidth =
                        if (actionWidth == 0) {
                            0
                        } else {
                            navigationIconPlaceable.width
                        }
                    val halfOfAppBar = constraints.maxWidth / 2
                    var baseX =
                        ((halfOfAppBar - titlePlaceable.width / 2) + halfOfAppBar * bias).roundToInt()
                    if (baseX < navigationIconWidth) {
                        // May happen if the navigation is wider than the actions and the
                        // title is long. In this case, prioritize showing more of the title
                        // by
                        // offsetting it to the right.
                        baseX += (navigationIconWidth - baseX)
                    } else if (
                        baseX + titlePlaceable.width >
                        constraints.maxWidth - actionWidth
                    ) {
                        // May happen if the actions are wider than the navigation and the
                        // title
                        // is long. In this case, offset to the left.
                        baseX +=
                            (
                                (constraints.maxWidth - actionWidth) -
                                    (baseX + titlePlaceable.width)
                                )
                    }
                    baseX.coerceIn(
                        TopmostAppBarDefaults.topAppBarTitleInset.roundToPx(),
                        max(
                            TopmostAppBarDefaults.topAppBarTitleInset.roundToPx(),
                            constraints.maxWidth - titlePlaceable.width - actionWidth
                        )
                    )
                },
                y = titleY
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (TopmostAppBarDefaults.height.roundToPx() - actionIconsPlaceable.height) / 2
            )
        }
    }
}

/**
 * This ease animation is so fast at beginning as we need this speed to neutralize some combination
 * of useless moves
 */
private val TopTitleAlphaEasing = CubicBezierEasing(.8f, 0f, .8f, .15f)

// ========= Animatable FreeForm Helper ==========

@Stable
class TopmostAppBarElementAnimationTimelineConfig internal constructor(
    defaultStart: TopmostAppBarAnimationTimeline,
    defaultAnimation: TopmostAppBarElementAnimation,
) {
    class Element internal constructor(
        internal var startAt: TopmostAppBarAnimationTimeline,
        internal var animation: TopmostAppBarElementAnimation,
    ) {
        fun toAnimatableProperty(): TopmostAppBarAnimatableProperty {
            return TopmostAppBarAnimatableProperty(
                animation = animation,
                timeline = startAt
            )
        }
    }

    val backgroundAlpha: Element = Element(defaultStart, defaultAnimation)
    val titleAlpha: Element = Element(defaultStart, defaultAnimation)
    val dividerAlpha: Element = Element(defaultStart, defaultAnimation)

    infix fun Element.at(startAt: TopmostAppBarAnimationTimeline): Element {
        this.startAt = startAt
        return this
    }

    infix fun Element.with(animation: AnimationSpec<Float>) {
        this.animation = TopmostAppBarElementAnimation.TimeBased(animation)
    }

    infix fun Element.animateProgress(transformer: (Float) -> Float) {
        this.animation = TopmostAppBarElementAnimation.ProgressBased(transformer)
    }
}

@Composable
fun topmostAppBarAnimatableProperties(
    defaultStart: TopmostAppBarAnimationTimeline = TopmostAppBarAnimationTimeline.Beginning,
    defaultAnimation: TopmostAppBarElementAnimation = TopmostAppBarElementAnimation.TimeBased(spring()),
    block: (TopmostAppBarElementAnimationTimelineConfig.() -> Unit)? = null,
): TopmostAppBarAnimatableProperties {
    return remember(block) {
        val config = TopmostAppBarElementAnimationTimelineConfig(defaultStart, defaultAnimation)
        block?.let { config.it() }
        TopmostAppBarAnimatableProperties(
            backgroundAlpha = config.backgroundAlpha.toAnimatableProperty(),
            titleAlpha = config.titleAlpha.toAnimatableProperty(),
            dividerAlpha = config.dividerAlpha.toAnimatableProperty(),
        )
    }
}

@Preview
@Composable
private fun TopmostAppBarPreview() {
    AppTheme {
        TopmostAppBar(
            title = "Hello world"
        )
    }
}

@Preview
@Composable
private fun TopmostAppBarWithNavigationPreview() {
    AppTheme {
        TopmostAppBar(
            title = "Hello world",
            navigationIcon = {
                IconButton({}) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            },
            actions = {
                IconButton({}) {
                    Icon(Icons.Default.Info, null)
                }
            }
        )
    }
}

// @Preview
// @Composable
// private fun MediumTopmostAppBarPreview() {
//    AppTheme {
//        val state = rememberTopmostAppBarState()
//        Scaffold(
//            topBar = {
//                MediumTopmostAppBar(
//                    state = state,
//                    title = "Hello World",
//                )
//            },
//            modifier = Modifier.nestedScroll(state.nestedScrollConnection)
//        ) { padding ->
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding)
//            ) {
//                items(100) {
//                    Text("Scroll Here.")
//                }
//            }
//        }
//    }
// }

@Preview
@Composable
private fun MediumTopmostAppBarWithNavigationPreview() {
    AppTheme {
        val state = rememberTopmostAppBarState()
        Scaffold(
            topBar = {
                MediumTopmostAppBar(
                    state = state,
                    title = {
                        Text("Hello World")
                    },
                    smallTitle = {
                        Text("Hi there!")
                    },
                    navigationIcon = {
                        IconButton({}) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            },
            modifier = Modifier.nestedScroll(state.nestedScrollConnection)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(100) {
                    Text("Scroll Here.")
                }
            }
        }
    }
}

@Preview
@Composable
private fun LargeTopmostAppBarPreview() {
    AppTheme {
        val state = rememberTopmostAppBarState()
        Scaffold(
            topBar = {
                LargeTopmostAppBar(
                    state = state,
                    title = {
                        Text("Hello World")
                    },
                    draggable = true,
                    snapInDraggableArea = false,
                    animatableProperties = topmostAppBarAnimatableProperties(
                        defaultStart = TopmostAppBarAnimationTimeline.Beginning,
                        defaultAnimation = TopmostAppBarElementAnimation.TimeBased(spring())
                    ) {
                        backgroundAlpha at TopmostAppBarAnimationTimeline.Collapsed
                        titleAlpha at TopmostAppBarAnimationTimeline.ScrolledAfterCollapsed
                        dividerAlpha animateProgress LinearOutSlowInEasing::transform
                    },
                    navigationIcon = {},
                    contentScrollBehaviour = TopmostAppBarContentScrollBehaviour.Scroll
                ) { paddings, fraction ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFB0A8EF))
                            .padding(paddings.topAppbarPadding)
                            .height(300.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("Big Smile", modifier = Modifier.align(Alignment.Center))
                        Text(
                            "Look here ${((1 - fraction) * 100).toInt()}% remain",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            },
            modifier = Modifier.nestedScroll(state.nestedScrollConnection)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(100) {
                    Text("Scroll Here.")
                }
            }
        }
    }
}
