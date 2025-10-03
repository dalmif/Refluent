package io.kayt.refluent.feature.welcome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.kayt.refluent.core.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val state = MutableStateFlow(WelcomeUiState(""))

    fun onNameChange(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun saveUsername() {
        userRepository.storeUsername(state.value.name)
    }
}

data class WelcomeUiState(
    val name: String
)