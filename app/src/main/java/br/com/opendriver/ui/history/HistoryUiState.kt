package br.com.opendriver.ui.history

import br.com.opendriver.domain.model.TripOffer

data class HistoryUiState(
    val trips: List<TripOffer> = emptyList(),
    val isLoading: Boolean = false
)
