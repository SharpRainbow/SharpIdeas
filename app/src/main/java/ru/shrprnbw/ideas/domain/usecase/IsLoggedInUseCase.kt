package ru.shrprnbw.ideas.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: CredentialsRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return authRepository.getToken().map { it.isNotBlank() }
    }
}