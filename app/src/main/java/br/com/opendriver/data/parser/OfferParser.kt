package br.com.opendriver.data.parser

import br.com.opendriver.domain.model.TripOffer

object OfferParser {

    val SUPPORTED_PACKAGES = setOf(
        "com.ubercab.driver",
        "com.app99.driver",
        "br.com.motoristaone"
    )

    private val UBER_WORD_PATTERN = Regex("\\buber\\b", RegexOption.IGNORE_CASE)
    private val STOPS_PATTERN = Regex(
        "\\b(\\d+)\\s*(?:paradas?|parada\\(s\\)|stops?)\\b",
        setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    fun parse(rawText: String, packageName: String): TripOffer? {
        if (rawText.isBlank()) return null

        return when {
            packageName == "com.ubercab.driver" || UBER_WORD_PATTERN.containsMatchIn(rawText) -> {
                parseUber(rawText)
            }
            packageName.contains("99.driver") || rawText.lowercase().contains("99") -> {
                parse99(rawText)
            }
            else -> {
                parseGeneric(rawText, packageName)
            }
        }
    }

    private fun parseUber(text: String): TripOffer? {
        val value = extractValue(text) ?: return null
        val distance = extractDistance(text) ?: return null
        val minutes = extractMinutes(text) ?: 15 // Default fallback se não achar o tempo

        val rating = extractRating(text) ?: 4.8f
        val hasStops = STOPS_PATTERN.containsMatchIn(text)
        val legCount = STOPS_PATTERN.find(text)?.groupValues?.get(1)?.toIntOrNull()?.let { it + 1 } ?: 2

        val (origin, dest) = extractAddresses(text)

        return TripOffer(
            valueRS = value,
            distanceKm = distance,
            estimatedMinutes = minutes,
            passengerRating = rating,
            platform = "com.ubercab.driver",
            origin = origin,
            destination = dest,
            hasStops = hasStops,
            legCount = legCount
        )
    }

    private fun parse99(text: String): TripOffer? {
        val value = extractValue(text) ?: return null
        val distance = extractDistance(text) ?: return null
        val minutes = extractMinutes(text) ?: 15

        val rating = extractRating(text) ?: 4.8f
        val hasStops = STOPS_PATTERN.containsMatchIn(text)
        val legCount = STOPS_PATTERN.find(text)?.groupValues?.get(1)?.toIntOrNull()?.let { it + 1 } ?: 2

        val (origin, dest) = extractAddresses(text)

        return TripOffer(
            valueRS = value,
            distanceKm = distance,
            estimatedMinutes = minutes,
            passengerRating = rating,
            platform = "com.app99.driver",
            origin = origin,
            destination = dest,
            hasStops = hasStops,
            legCount = legCount
        )
    }

    private fun parseGeneric(text: String, packageName: String): TripOffer? {
        val value = extractValue(text) ?: return null
        val distance = extractDistance(text) ?: return null
        val minutes = extractMinutes(text) ?: 10

        val rating = extractRating(text) ?: 5.0f
        val hasStops = STOPS_PATTERN.containsMatchIn(text)

        val (origin, dest) = extractAddresses(text)

        return TripOffer(
            valueRS = value,
            distanceKm = distance,
            estimatedMinutes = minutes,
            passengerRating = rating,
            platform = packageName,
            origin = origin,
            destination = dest,
            hasStops = hasStops,
            legCount = if (hasStops) 3 else 2
        )
    }

    private fun extractValue(text: String): Float? {
        // Busca formatos comuns de moeda: R$ 15,90 ou R$15.90 ou simplesmente 15,90 em certas posições
        val regex = Regex("R\\$\\s*(\\d+[,.]\\d{2})|R\\$\\s*(\\d+)")
        val match = regex.find(text) ?: return null
        val valueStr = match.groupValues.firstOrNull { it.isNotBlank() && it != match.value } ?: return null
        return valueStr.replace(",", ".").toFloatOrNull()
    }

    private fun extractDistance(text: String): Float? {
        // Busca formatos comuns: 5,4 km ou 5.4km ou 12 km
        val regex = Regex("(\\d+[,.]?\\d*)\\s*km", RegexOption.IGNORE_CASE)
        val match = regex.find(text) ?: return null
        return match.groupValues[1].replace(",", ".").toFloatOrNull()
    }

    private fun extractMinutes(text: String): Int? {
        // Busca formatos comuns: 14 min ou 1 hora e 15 min ou 1h 15m
        val regexMin = Regex("(\\d+)\\s*(?:min|m\\b)", RegexOption.IGNORE_CASE)
        val matchMin = regexMin.find(text)

        val regexHour = Regex("(\\d+)\\s*(?:h|hora|horas)", RegexOption.IGNORE_CASE)
        val matchHour = regexHour.find(text)

        var totalMinutes = 0
        if (matchHour != null) {
            val hours = matchHour.groupValues[1].toIntOrNull() ?: 0
            totalMinutes += hours * 60
        }
        if (matchMin != null) {
            val mins = matchMin.groupValues[1].toIntOrNull() ?: 0
            totalMinutes += mins
        }

        return if (totalMinutes > 0) totalMinutes else null
    }

    private fun extractRating(text: String): Float? {
        // Busca formatos comuns de nota de passageiro: 4.85 ou 4,9 ou 5.0
        val regex = Regex("\\b([345][,.]\\d{1,2})\\b")
        val match = regex.find(text) ?: return null
        return match.groupValues[1].replace(",", ".").toFloatOrNull()
    }

    private fun extractAddresses(text: String): Pair<String, String> {
        // Divisão de origem e destino
        // Tenta achar padrões comuns como "Origem: ... Destino: ..." ou "De ... Para ..."
        // Se falhar, tenta achar linhas textuais longas que parecem logradouros usando o POI_PATTERN/LOGRADOURO_PREFIX
        val lines = text.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.contains("R$") && !it.contains("km") && !it.contains("min") }

        val origin = lines.getOrNull(0) ?: "Origem desconhecida"
        val destination = lines.getOrNull(1) ?: "Destino desconhecido"
        return Pair(origin, destination)
    }
}
