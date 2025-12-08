package io.kayt.refluent.feature.home.liveedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.model.LiveEditState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveEditViewModel @Inject constructor(
    private val liveEditRepository: LiveEditRepository
) :
    ViewModel() {

    val state =
        liveEditRepository.getLiveEditState().map { connectionState ->
            when (connectionState) {
                is LiveEditState.Disabled -> LiveEditUiState.Disconnected
                is LiveEditState.Enabled -> LiveEditUiState.Connected(connectionState.key)
                LiveEditState.Connecting -> LiveEditUiState.Connecting
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LiveEditUiState.Disconnected
        )

    private var isConnecting = false
    fun connect() {
        if (!isConnecting) {
            isConnecting = true
            viewModelScope.launch {
                try {
                    liveEditRepository.connect()
                } finally {
                    isConnecting = false
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            liveEditRepository.disconnect()
        }
    }
}

sealed class LiveEditUiState {
    data object Disconnected : LiveEditUiState()
    data class Connected(val connectionCode: String) : LiveEditUiState()
    data object Connecting : LiveEditUiState()
}