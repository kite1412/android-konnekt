package nrr.konnekt.authentication

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.usecase.LoginUseCase
import nrr.konnekt.core.domain.usecase.RegisterUseCase
import nrr.konnekt.core.domain.util.Result
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    internal var isSignIn by mutableStateOf(true)
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
                        && (username.isNotBlank() && username.length >= 5)
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
                actionState = if (u is Result.Error) ActionState.Error(
                    when (u.error) {
                        AuthError.EmailNotConfirmed -> "Email Not Confirmed"
                        else -> "Invalid email or password"
                    }
                ) else ActionState.Success
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
                if (u is Result.Error) actionState = ActionState.Error("Email is already taken")
                else {
                    verificationEmailSent = true
                    actionState = ActionState.Success
                }
            }
        }
    }
}