package br.com.opendriver.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import br.com.opendriver.domain.model.DriverSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "driver_preferences")

class DriverPreferencesDataSource(private val context: Context) {

    companion object {
        val COST_PER_KM = floatPreferencesKey("cost_per_km")
        val GREEN_PER_KM = floatPreferencesKey("green_per_km")
        val RED_PER_KM = floatPreferencesKey("red_per_km")
        val GREEN_PER_HOUR = floatPreferencesKey("green_per_hour")
        val RED_PER_HOUR = floatPreferencesKey("red_per_hour")
        val GREEN_RATING = floatPreferencesKey("green_rating")
        val RED_RATING = floatPreferencesKey("red_rating")
        val OVERLAY_DURATION = intPreferencesKey("overlay_duration")
        val OVERLAY_OPACITY = floatPreferencesKey("overlay_opacity")
        val FONT_SIZE = intPreferencesKey("font_size")
        val OVERLAY_THEME = stringPreferencesKey("overlay_theme")
        val NOTIFY_VOICE = booleanPreferencesKey("notify_voice")
        val NOTIFY_TEXT = booleanPreferencesKey("notify_text")
        val COMPACT_OPACITY = floatPreferencesKey("compact_opacity")
        val PASSENGER_MSG_ENABLED = booleanPreferencesKey("passenger_msg_enabled")
        val PASSENGER_MSG = stringPreferencesKey("passenger_msg")
        val MULTICARD_ENABLED = booleanPreferencesKey("multicard_enabled")
        val SAVE_OFFER_PRINTS = booleanPreferencesKey("save_offer_prints")
    }

    val driverSettingsFlow: Flow<DriverSettings> = context.dataStore.data.map { preferences ->
        DriverSettings(
            costPerKm = preferences[COST_PER_KM] ?: 0.5f,
            greenPerKm = preferences[GREEN_PER_KM] ?: 1.5f,
            redPerKm = preferences[RED_PER_KM] ?: 0.8f,
            greenPerHour = preferences[GREEN_PER_HOUR] ?: 30f,
            redPerHour = preferences[RED_PER_HOUR] ?: 15f,
            greenRating = preferences[GREEN_RATING] ?: 4.7f,
            redRating = preferences[RED_RATING] ?: 4.5f,
            overlayDuration = preferences[OVERLAY_DURATION] ?: 8000,
            overlayOpacity = preferences[OVERLAY_OPACITY] ?: 0.9f,
            fontSize = preferences[FONT_SIZE] ?: 14,
            overlayTheme = preferences[OVERLAY_THEME] ?: "dark",
            notifyVoice = preferences[NOTIFY_VOICE] ?: true,
            notifyText = preferences[NOTIFY_TEXT] ?: true,
            compactOpacity = preferences[COMPACT_OPACITY] ?: 0.7f,
            passengerMsgEnabled = preferences[PASSENGER_MSG_ENABLED] ?: false,
            passengerMsg = preferences[PASSENGER_MSG] ?: "",
            multicardEnabled = preferences[MULTICARD_ENABLED] ?: false,
            saveOfferPrints = preferences[SAVE_OFFER_PRINTS] ?: false
        )
    }

    suspend fun saveSettings(settings: DriverSettings) {
        context.dataStore.edit { preferences ->
            preferences[COST_PER_KM] = settings.costPerKm
            preferences[GREEN_PER_KM] = settings.greenPerKm
            preferences[RED_PER_KM] = settings.redPerKm
            preferences[GREEN_PER_HOUR] = settings.greenPerHour
            preferences[RED_PER_HOUR] = settings.redPerHour
            preferences[GREEN_RATING] = settings.greenRating
            preferences[RED_RATING] = settings.redRating
            preferences[OVERLAY_DURATION] = settings.overlayDuration
            preferences[OVERLAY_OPACITY] = settings.overlayOpacity
            preferences[FONT_SIZE] = settings.fontSize
            preferences[OVERLAY_THEME] = settings.overlayTheme
            preferences[NOTIFY_VOICE] = settings.notifyVoice
            preferences[NOTIFY_TEXT] = settings.notifyText
            preferences[COMPACT_OPACITY] = settings.compactOpacity
            preferences[PASSENGER_MSG_ENABLED] = settings.passengerMsgEnabled
            preferences[PASSENGER_MSG] = settings.passengerMsg
            preferences[MULTICARD_ENABLED] = settings.multicardEnabled
            preferences[SAVE_OFFER_PRINTS] = settings.saveOfferPrints
        }
    }
}
