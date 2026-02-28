package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.model.UserEdit
import nrr.konnekt.core.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        payload: UserEdit
    ) = userRepository.updateCurrentUser(payload)
}