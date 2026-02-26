package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class InvalidateGroupUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke() = userRepository.invalidateGroupUsers()

}