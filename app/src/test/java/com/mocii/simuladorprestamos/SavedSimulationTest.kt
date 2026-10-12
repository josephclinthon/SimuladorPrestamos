package com.mocii.simuladorprestamos

import com.mocii.simuladorprestamos.ui.prestamo.SavedSimulation
import org.junit.Assert.*
import org.junit.Test

class SavedSimulationTest {
    private val inputs = mapOf(
        "capital" to "1000", "tasa" to "12", "numeroCuotas" to "12",
        "fechaSolicitud" to "31/01/2026", "diaPago" to "31",
        "porcentajeSeguro01" to "0.1", "annualBase" to "360",
        "period" to "30", "paymentDayType" to "01"
    )

    @Test fun reopeningUsesAllSavedInputs() {
        val record = SavedSimulation(inputs = inputs)
        val original = record.calculate()
        val reopened = record.copy(inputs = inputs.toMap()).calculate()
        assertEquals(original, reopened)
        assertEquals(12, reopened.cuotas.size)
        assertTrue(reopened.totalSeguros.signum() > 0)
        assertEquals(0, reopened.totalCapital.compareTo("1000".toBigDecimal()))
    }

    @Test fun changingInsuranceChangesComparisonTotals() {
        val insured = SavedSimulation(inputs = inputs).calculate()
        val uninsured = SavedSimulation(inputs = inputs + ("porcentajeSeguro01" to "")).calculate()
        assertEquals(0, uninsured.totalSeguros.signum())
        assertTrue(insured.totalSeguros > uninsured.totalSeguros)
    }

    @Test(expected = java.time.format.DateTimeParseException::class)
    fun rejectsImpossibleSavedDate() {
        SavedSimulation(inputs = inputs + ("fechaSolicitud" to "31/02/2026")).calculate()
    }
}
