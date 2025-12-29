package io.kayt.refluent.core.data

import io.kayt.core.model.DarkModeType
import io.kayt.refluent.core.data.storage.UserStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepository @Inject constructor(
    private val userStorage: UserStorage
) {

    private val darkModeFlow = MutableStateFlow(DarkModeType.System)

    fun getDarkModeType(): DarkModeType {
        return userStorage.getDarkMode().also { darkModeFlow.value = it }
    }

    fun darkModeType(): Flow<DarkModeType> = flow {
        emitAll(darkModeFlow)
    }

    suspend fun setDarkModeType(darkModeType: DarkModeType) {
        darkModeFlow.emit(darkModeType)
        withContext(Dispatchers.IO) {
            userStorage.saveDarkMode(darkModeType)
        }
    }
}