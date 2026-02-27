package nrr.konnekt.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    authentication: Authentication
) : ViewModel() {
    internal val currentUser = authentication
        .loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}