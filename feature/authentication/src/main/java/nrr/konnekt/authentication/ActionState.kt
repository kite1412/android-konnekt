package nrr.konnekt.authentication

internal sealed interface ActionState {
    object Performing : ActionState
    object Success : ActionState
    data class Error(val message: String) : ActionState
}