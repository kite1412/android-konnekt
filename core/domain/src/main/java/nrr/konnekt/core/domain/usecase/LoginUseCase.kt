package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authentication: Authentication
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): User? = authentication.login(
        email = email,
        password = password
    )
}