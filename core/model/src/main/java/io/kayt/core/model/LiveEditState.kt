package io.kayt.core.model

sealed interface LiveEditState {
    data class Disabled(val message: String? = null) : LiveEditState
    data class Enabled(val key: String) : LiveEditState
    data object Connecting : LiveEditState
}