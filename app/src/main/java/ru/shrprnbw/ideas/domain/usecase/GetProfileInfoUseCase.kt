package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class GetProfileInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): User {
        return userRepository.getUserInfo()
    }

}