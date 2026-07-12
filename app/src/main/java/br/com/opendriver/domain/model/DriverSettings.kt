package br.com.opendriver.domain.model

data class DriverSettings(
    val costPerKm: Float = 0.5f,
    val greenPerKm: Float = 1.5f,
    val redPerKm: Float = 0.8f,
    val greenPerHour: Float = 30f,
    val redPerHour: Float = 15f,
    val greenRating: Float = 4.7f,
    val redRating: Float = 4.5f,
    val overlayDuration: Int = 8000,
    val overlayOpacity: Float = 0.9f,
    val fontSize: Int = 14,
    val overlayTheme: String = "dark",
    val notifyVoice: Boolean = true,
    val notifyText: Boolean = true,
    val compactOpacity: Float = 0.7f,
    val passengerMsgEnabled: Boolean = false,
    val passengerMsg: String = "",
    val multicardEnabled: Boolean = false,
    val saveOfferPrints: Boolean = false
)
