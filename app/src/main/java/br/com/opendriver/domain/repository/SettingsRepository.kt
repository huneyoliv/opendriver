package br.com.opendriver.domain.repository

import br.com.opendriver.domain.model.DriverSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<DriverSettings>
    suspend fun saveSettings(settings: DriverSettings)
}
