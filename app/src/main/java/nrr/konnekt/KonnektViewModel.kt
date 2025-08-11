package nrr.konnekt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.ImagePathResolver
import javax.inject.Inject

@HiltViewModel
internal class KonnektViewModel @Inject constructor(
    authentication: Authentication,
    internal val imageResolver: ImagePathResolver
) : ViewModel() {
    var showSplashOnce by mutableStateOf(false)
    val isSignedIn = authentication.loggedInUser
        .map {
            it != null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}