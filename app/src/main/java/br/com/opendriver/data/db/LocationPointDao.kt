package br.com.opendriver.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: LocationPointEntity): Long

    @Query("SELECT * FROM location_points WHERE recordedAt >= :startOfDay ORDER BY recordedAt ASC")
    fun observePointsFrom(startOfDay: Long): Flow<List<LocationPointEntity>>

    @Query("DELETE FROM location_points WHERE recordedAt >= :startOfDay")
    suspend fun clearDayPoints(startOfDay: Long)
}
