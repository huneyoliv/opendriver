package br.com.opendriver.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripOfferDao {
    @Query("SELECT * FROM trip_offers ORDER BY capturedAt DESC")
    fun observeAll(): Flow<List<TripOfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TripOfferEntity): Long

    @Query("DELETE FROM trip_offers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM trip_offers")
    suspend fun clearAll()

    @Query("SELECT COALESCE(SUM(valueRS), 0.0) FROM trip_offers WHERE capturedAt >= :startOfDay")
    fun observeDayEarnings(startOfDay: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM trip_offers WHERE capturedAt >= :startOfDay")
    fun observeDayTripsCount(startOfDay: Long): Flow<Int>
}
