package br.com.opendriver.domain.repository

import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.GradeResult
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeAll(): Flow<List<TripOffer>>
    suspend fun save(offer: TripOffer, gradeResult: GradeResult)
    suspend fun delete(id: Long)
    suspend fun clearAll()
    fun observeDayEarnings(startOfDayTimestamp: Long): Flow<Float>
    fun observeDayTripsCount(startOfDayTimestamp: Long): Flow<Int>
}
