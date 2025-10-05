package io.kayt.refluent

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import io.kayt.refluent.core.data.UserRepository
import io.kayt.refluent.core.ui.misc.provideTTSManager
import io.kayt.refluent.core.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, 0x4F000000),
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, 0x4F000000)
        )
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                CompositionLocalProvider(provideTTSManager()) {
                    MainUi(isLoggedIn = userRepository.getUsername() != null)
                }
            }
        }
    }
}