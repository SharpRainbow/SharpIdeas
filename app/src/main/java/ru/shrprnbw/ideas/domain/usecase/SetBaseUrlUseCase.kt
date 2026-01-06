package ru.shrprnbw.ideas.domain.usecase

import android.util.Log
import ru.shrprnbw.ideas.domain.repository.SettingsRepository
import javax.inject.Inject

class SetBaseUrlUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(baseUrl: String) {
        Log.d("SetBaseUrlUseCase", "Setting base URL to: $baseUrl")
        settingsRepository.saveBaseUrl(baseUrl)
    }

}