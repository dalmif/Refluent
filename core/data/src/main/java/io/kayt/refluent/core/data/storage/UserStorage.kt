package io.kayt.refluent.core.data.storage

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import io.kayt.core.model.DarkModeType
import javax.inject.Inject

class UserStorage @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    companion object {
        const val LIVE_EDIT_KEY = "liveEditKey"
        const val DARK_MODE = "darkMode"
    }

    fun saveLiveEditKey(key: String) {
        sharedPreferences.edit {
            putString(LIVE_EDIT_KEY, key)
        }
    }

    fun clearLiveEditKey() {
        sharedPreferences.edit {
            remove(LIVE_EDIT_KEY)
        }
    }

    fun getLastLiveEditKey(): String? {
        return sharedPreferences.getString(LIVE_EDIT_KEY, null)
    }

    fun saveDarkMode(darkModeType: DarkModeType) {
        sharedPreferences.edit {
            putInt(
                DARK_MODE,
                darkModeType.toInt()
            )
        }
    }

    fun getDarkMode(): DarkModeType {
        return sharedPreferences.getInt(DARK_MODE, 0).toDarkModeType()
    }

    private fun DarkModeType.toInt(): Int {
        return when (this) {
            DarkModeType.Dark -> 2
            DarkModeType.Light -> 1
            DarkModeType.System -> 0
        }
    }

    private fun Int.toDarkModeType(): DarkModeType {
        return when (this) {
            2 -> DarkModeType.Dark
            1 -> DarkModeType.Light
            0 -> DarkModeType.System
            else -> error("Invalid value")
        }
    }
}
