package nrr.konnekt.authentication

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.usecase.LoginUseCase
import nrr.konnekt.core.domain.usecase.RegisterUseCase

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    internal var isSignIn by mutableStateOf(false)
    internal var verificationEmailSent by mutableStateOf(false)
    internal var email by mutableStateOf("")
    internal var username by mutableStateOf("")
    internal var password by mutableStateOf("")
    internal var confirmPassword by mutableStateOf("")
    internal var actionState by mutableStateOf<ActionState?>(null)
    internal val actionEnabled by derivedStateOf {
        (
            (isSignIn && email.isNotBlank() && password.isNotBlank())
                || (email.isNotBlank()
                        && username.isNotBlank()
                        && password.isNotBlank()
                        && password == confirmPassword
                        && confirmPassword.isNotBlank()
                    )
        )
            && password.length >= 6
            && (actionState != ActionState.Success && actionState != ActionState.Performing)
    }

    internal fun login() {
        viewModelScope.launch {
            if (actionEnabled) {
                actionState = ActionState.Performing
                val u = loginUseCase(
                    email = email,
                    password = password
                )
                actionState = if (u == null) ActionState.Error("Email or password is incorrect")
                    else ActionState.Success
            }
        }
    }

    internal fun register() {
        viewModelScope.launch {
            if (actionEnabled) {
                actionState = ActionState.Performing
                val u = registerUseCase(
                    email = email,
                    username = username,
                    password = password
                )
                if (u == null) actionState = ActionState.Error("Email is already taken")
                else {
                    verificationEmailSent = true
                    actionState = ActionState.Success
                }
            }
        }
    }
}