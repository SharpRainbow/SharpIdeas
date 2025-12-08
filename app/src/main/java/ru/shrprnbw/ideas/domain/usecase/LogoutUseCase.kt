package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        authRepository.logout()
    }

}