package nrr.konnekt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.util.AuthStatus
import nrr.konnekt.core.network.upload.domain.FileNameFormatter
import nrr.konnekt.core.network.upload.domain.FileResolver
import nrr.konnekt.core.network.upload.domain.FileUploadConstraints
import nrr.konnekt.core.storage.file.FileCache
import nrr.konnekt.core.ui.util.ValueManager
import javax.inject.Inject

@HiltViewModel
internal class KonnektViewModel @Inject constructor(
    authentication: Authentication,
    internal val fileResolver: FileResolver,
    internal val userPresenceManager: UserPresenceManager,
    internal val fileNameFormatter: FileNameFormatter,
    internal val fileCache: FileCache,
    internal val fileUploadConstraints: FileUploadConstraints
) : ViewModel() {
    var showSplashOnce by mutableStateOf(false)
    var statusBarColor by mutableStateOf(Color.Transparent)
        private set
    var navigationBarColor by mutableStateOf(Color.Transparent)
        private set

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

    val statusBarColorManager = object : ValueManager<Color> {
        override fun update(newValue: Color): Color {
            statusBarColor = newValue
            return statusBarColor
        }

        override fun reset(): Color {
            statusBarColor = Color.Transparent
            return statusBarColor
        }
    }

    val navigationBarColorManager = object : ValueManager<Color> {
        override fun update(newValue: Color): Color {
            navigationBarColor = newValue
            return navigationBarColor
        }

        override fun reset(): Color {
            navigationBarColor = Color.Transparent
            return navigationBarColor
        }
    }
}