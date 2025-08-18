package nrr.konnekt.feature.conversation.util

internal sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
}