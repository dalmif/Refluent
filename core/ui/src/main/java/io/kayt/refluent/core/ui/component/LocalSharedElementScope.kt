package io.kayt.refluent.core.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

@SuppressLint("ComposeCompositionLocalUsage")
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope> { error("Not Provided") }

@SuppressLint("ComposeCompositionLocalUsage")
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> { error("Not Provided") }
