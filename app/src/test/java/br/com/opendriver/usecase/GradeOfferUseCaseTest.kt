package br.com.opendriver.usecase

import br.com.opendriver.domain.model.DriverSettings
import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.TripGrade
import br.com.opendriver.domain.repository.SettingsRepository
import br.com.opendriver.domain.usecase.GradeOfferUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GradeOfferUseCaseTest {

    private class FakeSettingsRepository(private val settings: DriverSettings) : SettingsRepository {
        override fun observeSettings(): Flow<DriverSettings> = flowOf(settings)
        override suspend fun saveSettings(settings: DriverSettings) {}
    }

    @Test
    fun grade_green_km_and_green_hour_returns_green_final() = runTest {
        val settings = DriverSettings(
            greenPerKm = 1.5f,
            redPerKm = 1.0f,
            greenPerHour = 30f,
            redPerHour = 20f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // R$ 20.00 por 10 km (2.0 R$/km) e 30 minutos (40.0 R$/hora)
        val offer = TripOffer(
            valueRS = 20f,
            distanceKm = 10f,
            estimatedMinutes = 30,
            passengerRating = 4.8f,
            platform = "com.ubercab.driver",
            origin = "Origem",
            destination = "Destino",
            hasStops = false
        )

        val result = useCase(offer)

        assertEquals(TripGrade.GREEN, result.gradeKm)
        assertEquals(TripGrade.GREEN, result.gradeHour)
        assertEquals(TripGrade.GREEN, result.finalGrade)
    }

    @Test
    fun grade_green_km_and_red_hour_returns_yellow_final() = runTest {
        val settings = DriverSettings(
            greenPerKm = 1.5f,
            redPerKm = 1.0f,
            greenPerHour = 30f,
            redPerHour = 20f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // R$ 20.00 por 10 km (2.0 R$/km - Verde) e 90 minutos (13.3 R$/hora - Vermelho)
        val offer = TripOffer(
            valueRS = 20f,
            distanceKm = 10f,
            estimatedMinutes = 90,
            passengerRating = 4.8f,
            platform = "com.ubercab.driver",
            origin = "Origem",
            destination = "Destino",
            hasStops = false
        )

        val result = useCase(offer)

        assertEquals(TripGrade.GREEN, result.gradeKm)
        assertEquals(TripGrade.RED, result.gradeHour)
        assertEquals(TripGrade.YELLOW, result.finalGrade)
    }

    @Test
    fun grade_red_km_returns_red_final_regardless_of_hour() = runTest {
        val settings = DriverSettings(
            greenPerKm = 2.0f,
            redPerKm = 1.2f,
            greenPerHour = 30f,
            redPerHour = 20f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // R$ 10.00 por 10 km (1.0 R$/km - Vermelho) e 10 minutos (60 R$/hora - Verde)
        val offer = TripOffer(
            valueRS = 10f,
            distanceKm = 10f,
            estimatedMinutes = 10,
            passengerRating = 4.8f,
            platform = "com.ubercab.driver",
            origin = "Origem",
            destination = "Destino",
            hasStops = false
        )

        val result = useCase(offer)

        assertEquals(TripGrade.RED, result.gradeKm)
        assertEquals(TripGrade.GREEN, result.gradeHour)
        assertEquals(TripGrade.RED, result.finalGrade)
    }

    @Test
    fun grade_rating_evaluates_correctly() = runTest {
        val settings = DriverSettings(
            greenRating = 4.8f,
            redRating = 4.5f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // Verde (>= 4.8)
        val offerGreen = TripOffer(10f, 5f, 10, 4.8f, "com.ubercab.driver", "A", "B", false)
        assertEquals(TripGrade.GREEN, useCase(offerGreen).gradeRating)

        // Amarelo (< 4.8 e >= 4.5)
        val offerYellow = TripOffer(10f, 5f, 10, 4.6f, "com.ubercab.driver", "A", "B", false)
        assertEquals(TripGrade.YELLOW, useCase(offerYellow).gradeRating)

        // Vermelho (< 4.5)
        val offerRed = TripOffer(10f, 5f, 10, 4.4f, "com.ubercab.driver", "A", "B", false)
        assertEquals(TripGrade.RED, useCase(offerRed).gradeRating)
    }

    @Test
    fun division_by_zero_safety_returns_zero_and_red_grades() = runTest {
        val settings = DriverSettings(
            greenPerKm = 1.5f,
            redPerKm = 1.0f,
            greenPerHour = 30f,
            redPerHour = 20f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // Corrida com distância e tempo zerados
        val offer = TripOffer(
            valueRS = 10f,
            distanceKm = 0f,
            estimatedMinutes = 0,
            passengerRating = 4.8f,
            platform = "com.ubercab.driver",
            origin = "A",
            destination = "B",
            hasStops = false
        )

        val result = useCase(offer)

        assertEquals(0f, result.lucroPerKm)
        assertEquals(0f, result.lucroPerHour)
        assertEquals(TripGrade.RED, result.gradeKm)
        assertEquals(TripGrade.RED, result.gradeHour)
        assertEquals(TripGrade.RED, result.finalGrade)
    }

    @Test
    fun exact_boundary_values_evaluate_as_green() = runTest {
        val settings = DriverSettings(
            greenPerKm = 2.0f,
            redPerKm = 1.0f,
            greenPerHour = 40f,
            redPerHour = 20f
        )
        val useCase = GradeOfferUseCase(FakeSettingsRepository(settings))

        // Exatamente R$ 2.0/km e R$ 40/hora
        val offer = TripOffer(
            valueRS = 10f,
            distanceKm = 5f, // 10 / 5 = 2.0
            estimatedMinutes = 15, // 15 mins = 0.25h. 10 / 0.25 = 40.0
            passengerRating = 4.8f,
            platform = "com.ubercab.driver",
            origin = "A",
            destination = "B",
            hasStops = false
        )

        val result = useCase(offer)

        assertEquals(TripGrade.GREEN, result.gradeKm)
        assertEquals(TripGrade.GREEN, result.gradeHour)
        assertEquals(TripGrade.GREEN, result.finalGrade)
    }
}
