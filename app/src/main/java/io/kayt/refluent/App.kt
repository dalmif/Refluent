package io.kayt.refluent

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.kayt.refluent.core.ui.theme.typography.setMixedFont

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setMixedFont(this)
    }
}