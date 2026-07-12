package br.com.opendriver.ui.dashboard

data class DashboardUiState(
    val dayEarnings: Float = 0f,
    val dayDistanceKm: Float = 0f,
    val netProfitPerKm: Float = 0f,
    val copilotActive: Boolean = false,
    val trackerActive: Boolean = false,
    val totalTripsToday: Int = 0
)
