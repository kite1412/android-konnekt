package nrr.konnekt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import nrr.konnekt.core.domain.Authentication
import javax.inject.Inject

@HiltViewModel
internal class KonnektViewModel @Inject constructor(
    authentication: Authentication
) : ViewModel() {
    var showSplashOnce by mutableStateOf(false)
    val isSignedIn = authentication
        .loggedInUser
        .map {
            it != null
        }
}