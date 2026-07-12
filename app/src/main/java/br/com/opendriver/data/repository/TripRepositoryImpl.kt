package br.com.opendriver.data.repository

import br.com.opendriver.data.db.TripOfferDao
import br.com.opendriver.data.db.TripOfferEntity
import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.TripGrade
import br.com.opendriver.domain.model.GradeResult
import br.com.opendriver.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val dao: TripOfferDao
) : TripRepository {

    override fun observeAll(): Flow<List<TripOffer>> {
        return dao.observeAll().map { entities ->
            entities.map { entity ->
                TripOffer(
                    id = entity.id,
                    valueRS = entity.valueRS,
                    distanceKm = entity.distanceKm,
                    estimatedMinutes = entity.estimatedMinutes,
                    passengerRating = entity.passengerRating,
                    platform = entity.platform,
                    origin = entity.origin,
                    destination = entity.destination,
                    hasStops = entity.hasStops,
                    legCount = entity.legCount,
                    finalGrade = try {
                        TripGrade.valueOf(entity.finalGrade)
                    } catch (e: Exception) {
                        TripGrade.YELLOW
                    },
                    lucroPerKm = entity.lucroPerKm,
                    lucroPerHour = entity.lucroPerHour,
                    capturedAt = entity.capturedAt,
                    offerPrintPath = entity.offerPrintPath
                )
            }
        }
    }

    override suspend fun save(offer: TripOffer, gradeResult: GradeResult) {
        val entity = TripOfferEntity(
            valueRS = offer.valueRS,
            distanceKm = offer.distanceKm,
            estimatedMinutes = offer.estimatedMinutes,
            passengerRating = offer.passengerRating,
            platform = offer.platform,
            origin = offer.origin,
            destination = offer.destination,
            hasStops = offer.hasStops,
            legCount = offer.legCount,
            finalGrade = gradeResult.finalGrade.name,
            lucroPerKm = gradeResult.lucroPerKm,
            lucroPerHour = gradeResult.lucroPerHour,
            capturedAt = offer.capturedAt,
            offerPrintPath = offer.offerPrintPath
        )
        dao.insert(entity)
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    override fun observeDayEarnings(startOfDayTimestamp: Long): Flow<Float> {
        return dao.observeDayEarnings(startOfDayTimestamp).map { it.toFloat() }
    }

    override fun observeDayTripsCount(startOfDayTimestamp: Long): Flow<Int> {
        return dao.observeDayTripsCount(startOfDayTimestamp)
    }
}
