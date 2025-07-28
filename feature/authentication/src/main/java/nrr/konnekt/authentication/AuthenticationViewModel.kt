package nrr.konnekt.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor() : ViewModel() {
    internal var isSignIn by mutableStateOf(false)
}