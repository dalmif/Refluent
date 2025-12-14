package io.kayt.refluent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.SyncLiveEditUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncLiveEditUseCase: SyncLiveEditUseCase
) : ViewModel() {

    fun keepLiveEditAlwaysSync() {
        viewModelScope.launch {
            // This function never return
            syncLiveEditUseCase.sync()
        }
    }
}