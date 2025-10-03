package io.kayt.refluent.core.data

import io.kayt.refluent.core.data.storage.UserStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userStorage: UserStorage) {

    fun storeUsername(name: String) {
        userStorage.saveUsername(name)
    }

    fun getUsername(): String? {
        return userStorage.getUsername()
    }
}