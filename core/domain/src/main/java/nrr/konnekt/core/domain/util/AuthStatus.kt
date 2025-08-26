package nrr.konnekt.core.domain.util

import nrr.konnekt.core.model.User

sealed interface AuthStatus {
    data class Authenticated(val user: User) : AuthStatus
    object Unauthenticated : AuthStatus
    object Loading : AuthStatus
}