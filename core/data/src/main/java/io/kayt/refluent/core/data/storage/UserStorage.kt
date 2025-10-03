package io.kayt.refluent.core.data.storage

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserStorage @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    fun saveUsername(name: String) {
        sharedPreferences.edit {
            putString("name", name)
        }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("name", null)
    }
}