package io.kayt.refluent.core.data.storage

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserStorage @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    companion object {
        const val LIVE_EDIT_KEY = "liveEditKey"
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
}
