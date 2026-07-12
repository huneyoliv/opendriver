package br.com.opendriver.parser

import br.com.opendriver.data.parser.OfferParser
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class OfferParserTest {

    @Test
    fun parse_uber_offer_returns_correct_fields() {
        val rawText = """
            Nova oferta UberX
            R$ 15,90
            5.4 km - 14 min
            Avaliação: 4.85 ★
            Rua das Oliveiras, 123
            Avenida Central, 456
        """.trimIndent()

        val offer = OfferParser.parse(rawText, "com.ubercab.driver")
            ?: throw AssertionError("Offer should not be null")

        assertEquals(15.90f, offer.valueRS)
        assertEquals(5.4f, offer.distanceKm)
        assertEquals(14, offer.estimatedMinutes)
        assertEquals(4.85f, offer.passengerRating)
        assertEquals("com.ubercab.driver", offer.platform)
        assertEquals("Nova oferta UberX", offer.origin)
        assertEquals("Rua das Oliveiras, 123", offer.destination)
    }

    @Test
    fun parse_99_offer_returns_correct_fields() {
        val rawText = """
            Chamada 99Pop
            R$25.00
            12.0km - 25m
            Avaliação: 4.90
            De: Shopping Iguatemi
            Para: Aeroporto Internacional
        """.trimIndent()

        val offer = OfferParser.parse(rawText, "com.app99.driver")
            ?: throw AssertionError("Offer should not be null")

        assertEquals(25.00f, offer.valueRS)
        assertEquals(12.0f, offer.distanceKm)
        assertEquals(25, offer.estimatedMinutes)
        assertEquals(4.90f, offer.passengerRating)
        assertEquals("Shopping Iguatemi", offer.origin)
        assertEquals("Aeroporto Internacional", offer.destination)
    }

    @Test
    fun parse_with_stops_sets_hasStops_true() {
        val rawText = """
            UberX com 1 parada
            R$ 18,50
            8 km - 20 min
            4.8
        """.trimIndent()

        val offer = OfferParser.parse(rawText, "com.ubercab.driver")
            ?: throw AssertionError("Offer should not be null")

        assertTrue(offer.hasStops)
        assertEquals(2, offer.legCount)
    }

    @Test
    fun parse_blank_text_returns_null() {
        assertNull(OfferParser.parse("", "com.ubercab.driver"))
        assertNull(OfferParser.parse("   ", "com.ubercab.driver"))
    }

    @Test
    fun parse_with_mixed_time_formats() {
        val rawText = """
            R$ 45,00
            20 km - 1h 15 min
            4.95
        """.trimIndent()
        val offer = OfferParser.parse(rawText, "com.ubercab.driver")
            ?: throw AssertionError("Offer should not be null")

        assertEquals(45.00f, offer.valueRS)
        assertEquals(20.0f, offer.distanceKm)
        assertEquals(75, offer.estimatedMinutes)
        assertEquals(4.95f, offer.passengerRating)
    }

    @Test
    fun parse_malformed_text_returns_null_when_value_missing() {
        val rawText = """
            Corrida sem valor em dinheiro
            5 km - 10 min
            4.8
        """.trimIndent()
        val offer = OfferParser.parse(rawText, "com.ubercab.driver")
        assertNull(offer)
    }

    @Test
    fun parse_addresses_uses_fallbacks_correctly_when_missing() {
        val rawText = """
            R$ 10,00
            2.0 km - 5 min
        """.trimIndent()
        val offer = OfferParser.parse(rawText, "com.ubercab.driver")
            ?: throw AssertionError("Offer should not be null")

        assertEquals("Origem desconhecida", offer.origin)
        assertEquals("Destino desconhecido", offer.destination)
    }
}
