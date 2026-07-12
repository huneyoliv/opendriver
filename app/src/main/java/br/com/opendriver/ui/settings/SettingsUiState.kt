package br.com.opendriver.ui.settings

import br.com.opendriver.domain.model.DriverSettings

data class SettingsUiState(
    val settings: DriverSettings = DriverSettings(),
    val isSaved: Boolean = false
)
