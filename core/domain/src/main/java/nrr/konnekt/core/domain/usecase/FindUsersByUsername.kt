package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.UserRepository
import javax.inject.Inject

class FindUsersByUsername @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String) =
        userRepository.getUsersByUsername(username)
}