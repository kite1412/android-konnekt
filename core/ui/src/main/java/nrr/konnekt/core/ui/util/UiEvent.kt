package nrr.konnekt.core.ui.util

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
}