package br.com.opendriver.domain.usecase

import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.TripGrade
import br.com.opendriver.domain.model.GradeResult
import br.com.opendriver.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class GradeOfferUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(offer: TripOffer): GradeResult {
        val settings = settingsRepository.observeSettings().first()

        val lucroPerKm = if (offer.distanceKm > 0f) {
            offer.valueRS / offer.distanceKm
        } else {
            0f
        }

        val lucroPerHour = if (offer.estimatedMinutes > 0) {
            offer.valueRS / (offer.estimatedMinutes / 60f)
        } else {
            0f
        }

        // Lógica de Km
        val gradeKm = when {
            lucroPerKm >= settings.greenPerKm -> TripGrade.GREEN
            lucroPerKm < settings.redPerKm -> TripGrade.RED
            else -> TripGrade.YELLOW
        }

        // Lógica de Hora
        val gradeHour = when {
            lucroPerHour >= settings.greenPerHour -> TripGrade.GREEN
            lucroPerHour < settings.redPerHour -> TripGrade.RED
            else -> TripGrade.YELLOW
        }

        // Lógica de Avaliação de Passageiro
        val gradeRating = when {
            offer.passengerRating >= settings.greenRating -> TripGrade.GREEN
            offer.passengerRating < settings.redRating -> TripGrade.RED
            else -> TripGrade.YELLOW
        }

        // Ponderação final consolidada (baseado na lógica exata do smali calculateGrade)
        val finalGrade = when {
            gradeKm == TripGrade.GREEN && gradeHour == TripGrade.RED -> TripGrade.YELLOW
            gradeKm == TripGrade.GREEN && gradeHour != TripGrade.RED -> TripGrade.GREEN
            gradeKm == TripGrade.RED -> TripGrade.RED
            gradeKm == TripGrade.YELLOW && gradeHour == TripGrade.RED -> TripGrade.RED
            else -> TripGrade.YELLOW
        }

        return GradeResult(
            gradeKm = gradeKm,
            gradeHour = gradeHour,
            gradeRating = gradeRating,
            finalGrade = finalGrade,
            lucroPerKm = lucroPerKm,
            lucroPerHour = lucroPerHour
        )
    }
}
