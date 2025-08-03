package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.model.User
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authentication: Authentication
) {
    suspend operator fun invoke(
        email: String,
        username: String,
        password: String
    ): User? = authentication.register(
        email = email,
        username = username,
        password = password
    )
}