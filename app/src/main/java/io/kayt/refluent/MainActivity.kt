package io.kayt.refluent

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.computeWindowSizeClass
import androidx.window.layout.WindowMetricsCalculator
import dagger.hilt.android.AndroidEntryPoint
import io.kayt.refluent.core.ui.misc.provideTTSManager
import io.kayt.refluent.core.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = if (compactScreen()) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                CompositionLocalProvider(provideTTSManager()) {
                    MainUi()
                }
            }
        }
        viewModel.keepLiveEditAlwaysSync()
    }

    fun compactScreen(): Boolean {
        val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(this)
        val width = metrics.bounds.width()
        val height = metrics.bounds.height()
        val density = resources.displayMetrics.density
        val windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
            widthDp = width / density,
            heightDp = height / density
        )

        return !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) ||
                !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
    }
}
