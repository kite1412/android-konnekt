package nrr.konnekt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.util.AuthStatus
import javax.inject.Inject

@HiltViewModel
internal class KonnektViewModel @Inject constructor(
    authentication: Authentication,
    internal val fileResolver: FileResolver,
    internal val userPresenceManager: UserPresenceManager
) : ViewModel() {
    var showSplashOnce by mutableStateOf(false)

    val isSignedIn = combine(
        flow = authentication.authStatus,
        flow2 = authentication.loggedInUser
    ) { authStatus, loggedInUser ->
        if (authStatus == AuthStatus.Loading) null
        else authStatus is AuthStatus.Authenticated && loggedInUser != null
    }
        .onEach {
            if (it == true) userPresenceManager.markUserActive()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}