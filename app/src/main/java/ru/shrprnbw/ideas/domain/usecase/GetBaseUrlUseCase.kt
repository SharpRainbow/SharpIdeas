package ru.shrprnbw.ideas.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.repository.SettingsRepository
import javax.inject.Inject

class GetBaseUrlUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<String> {
        return settingsRepository.getBaseUrl()
    }

}