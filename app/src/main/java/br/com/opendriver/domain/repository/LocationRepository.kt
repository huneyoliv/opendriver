package br.com.opendriver.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun savePoint(latitude: Double, longitude: Double, speedKmh: Float)
    fun observeDayDistanceKm(startOfDayTimestamp: Long): Flow<Float>
    suspend fun clearDayPoints(startOfDayTimestamp: Long)
}
