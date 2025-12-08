package ru.shrprnbw.ideas.domain.usecase

import kotlinx.coroutines.flow.first
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) {

    suspend operator fun invoke(
        email: String,
        username: String
    ) {
        userRepository.updateUserInfo(email, username)
        val password = credentialsRepository.getSavedPassword().first()
        credentialsRepository.saveCredentials(
            login = email,
            password = password
        )
    }

}