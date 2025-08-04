package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authentication: Authentication
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User, AuthError> = authentication.login(
        email = email,
        password = password
    )
}