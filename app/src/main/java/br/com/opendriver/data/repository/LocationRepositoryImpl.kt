package br.com.opendriver.data.repository

import br.com.opendriver.data.db.LocationPointDao
import br.com.opendriver.data.db.LocationPointEntity
import br.com.opendriver.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.*

class LocationRepositoryImpl(
    private val dao: LocationPointDao
) : LocationRepository {

    override suspend fun savePoint(latitude: Double, longitude: Double, speedKmh: Float) {
        val point = LocationPointEntity(
            latitude = latitude,
            longitude = longitude,
            speedKmh = speedKmh
        )
        dao.insert(point)
    }

    override fun observeDayDistanceKm(startOfDayTimestamp: Long): Flow<Float> {
        return dao.observePointsFrom(startOfDayTimestamp).map { points ->
            if (points.size < 2) return@map 0f

            var totalDistance = 0.0
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                totalDistance += haversineDistance(
                    p1.latitude, p1.longitude,
                    p2.latitude, p2.longitude
                )
            }
            totalDistance.toFloat()
        }
    }

    override suspend fun clearDayPoints(startOfDayTimestamp: Long) {
        dao.clearDayPoints(startOfDayTimestamp)
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Raio da Terra em Km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
