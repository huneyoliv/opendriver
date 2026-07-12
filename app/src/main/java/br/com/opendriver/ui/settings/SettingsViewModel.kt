package br.com.opendriver.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.opendriver.domain.model.DriverSettings
import br.com.opendriver.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = repository.observeSettings()
        .map { settings -> SettingsUiState(settings = settings) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    fun updateSettings(settings: DriverSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
        }
    }
}
