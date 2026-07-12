package br.com.opendriver.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_offers")
data class TripOfferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val valueRS: Float,
    val distanceKm: Float,
    val estimatedMinutes: Int,
    val passengerRating: Float,
    val platform: String,
    val origin: String,
    val destination: String,
    val hasStops: Boolean,
    val legCount: Int,
    val finalGrade: String,
    val lucroPerKm: Float,
    val lucroPerHour: Float,
    val capturedAt: Long = System.currentTimeMillis(),
    val offerPrintPath: String? = null
)
