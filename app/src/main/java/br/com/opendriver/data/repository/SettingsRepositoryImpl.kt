package br.com.opendriver.data.repository

import br.com.opendriver.data.datastore.DriverPreferencesDataSource
import br.com.opendriver.domain.model.DriverSettings
import br.com.opendriver.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataSource: DriverPreferencesDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<DriverSettings> {
        return dataSource.driverSettingsFlow
    }

    override suspend fun saveSettings(settings: DriverSettings) {
        dataSource.saveSettings(settings)
    }
}
