package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        authRepository.register(
            username,
            email,
            password,
            firstName,
            lastName
        )
    }

}