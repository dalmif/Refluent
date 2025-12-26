package io.kayt.refluent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.SyncLiveEditUseCase
import io.kayt.core.model.DarkModeType
import io.kayt.refluent.core.data.UserPreferenceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncLiveEditUseCase: SyncLiveEditUseCase,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    val darkMode: StateFlow<DarkModeType> =
        userPreferenceRepository.darkModeType()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = DarkModeType.System
            )

    fun keepLiveEditAlwaysSync() {
        viewModelScope.launch {
            // This function never return
            syncLiveEditUseCase.sync()
        }
    }

    init {
        userPreferenceRepository.getDarkModeType()
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            when (darkMode.value) {
                DarkModeType.Dark -> userPreferenceRepository.setDarkModeType(DarkModeType.System)
                DarkModeType.Light -> userPreferenceRepository.setDarkModeType(DarkModeType.Dark)
                DarkModeType.System -> userPreferenceRepository.setDarkModeType(DarkModeType.Light)
            }
        }
    }
}