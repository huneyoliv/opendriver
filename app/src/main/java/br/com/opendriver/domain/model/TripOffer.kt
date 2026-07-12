package br.com.opendriver.domain.model

data class TripOffer(
    val id: Long = 0,
    val valueRS: Float,
    val distanceKm: Float,
    val estimatedMinutes: Int,
    val passengerRating: Float,
    val platform: String,
    val origin: String,
    val destination: String,
    val hasStops: Boolean,
    val legCount: Int = 2,
    val finalGrade: TripGrade? = null,
    val lucroPerKm: Float = 0f,
    val lucroPerHour: Float = 0f,
    val capturedAt: Long = System.currentTimeMillis(),
    val offerPrintPath: String? = null
)
