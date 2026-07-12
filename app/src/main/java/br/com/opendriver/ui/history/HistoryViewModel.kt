package br.com.opendriver.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.opendriver.domain.repository.TripRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = tripRepository.observeAll()
        .map { trips -> HistoryUiState(trips = trips) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState(isLoading = true)
        )

    fun deleteTrip(id: Long) {
        viewModelScope.launch {
            tripRepository.delete(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            tripRepository.clearAll()
        }
    }
}
