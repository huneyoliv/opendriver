package br.com.opendriver.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.opendriver.domain.repository.LocationRepository
import br.com.opendriver.domain.repository.SettingsRepository
import br.com.opendriver.domain.repository.TripRepository
import br.com.opendriver.domain.model.DriverSettings
import br.com.opendriver.service.FloatingButtonService
import kotlinx.coroutines.flow.*
import java.util.Calendar

class DashboardViewModel(
    private val tripRepository: TripRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _trackerActive = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        tripRepository.observeDayEarnings(getStartOfDayTimestamp()),
        tripRepository.observeDayTripsCount(getStartOfDayTimestamp()),
        locationRepository.observeDayDistanceKm(getStartOfDayTimestamp()),
        settingsRepository.observeSettings(),
        FloatingButtonService.copilotActiveFlow,
        _trackerActive
    ) { flowsArray ->
        val earnings = flowsArray[0] as Float
        val tripsCount = flowsArray[1] as Int
        val distance = flowsArray[2] as Float
        val settings = flowsArray[3] as DriverSettings
        val copilotActive = flowsArray[4] as Boolean
        val trackerActive = flowsArray[5] as Boolean

        // Lucro líquido real por km = ganhos / km total - custo do veículo
        val netProfit = if (distance > 0f) {
            (earnings / distance) - settings.costPerKm
        } else {
            0f
        }

        DashboardUiState(
            dayEarnings = earnings,
            dayDistanceKm = distance,
            netProfitPerKm = netProfit,
            copilotActive = copilotActive,
            trackerActive = trackerActive,
            totalTripsToday = tripsCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun setTrackerActive(active: Boolean) {
        _trackerActive.value = active
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
