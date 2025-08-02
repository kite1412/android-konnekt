package nrr.konnekt.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.usecase.LoginUseCase

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    internal var isSignIn by mutableStateOf(false)

    internal fun login() {
        viewModelScope.launch {

        }
    }
}