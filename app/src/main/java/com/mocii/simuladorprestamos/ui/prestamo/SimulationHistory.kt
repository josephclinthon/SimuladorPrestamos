package com.mocii.simuladorprestamos.ui.prestamo

import GeneradorPlanPagos
import ResultadoPlan
import Seguro
import android.content.Context
import android.content.Intent
import com.mocii.simuladorprestamos.common.utils.MUtils
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.util.UUID

data class SavedSimulation(
    val id: String = UUID.randomUUID().toString(),
    val updatedAt: Long = System.currentTimeMillis(),
    val inputs: Map<String, String>
) {
    fun calculate(): ResultadoPlan {
        fun value(key: String) = inputs.getValue(key)
        require(value("capital").toBigDecimal().signum() > 0)
        require(value("tasa").toBigDecimal().signum() > 0)
        require(value("numeroCuotas").toInt() > 0)
        require(value("diaPago").toInt() in 1..31)
        require(value("period").toInt() in listOf(30, 60, 90, 180, 360))
        require(value("annualBase") in listOf("360", "365"))
        java.time.LocalDate.parse(value("fechaSolicitud"), java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT))
        val insurance = value("porcentajeSeguro01").ifBlank { "0" }.toBigDecimal()
        require(insurance.signum() >= 0)
        val type = value("paymentDayType").take(2)
        require(type in listOf("01", "02", "03"))
        return GeneradorPlanPagos().generar(
            value("capital").toBigDecimal(), value("tasa").toBigDecimal(),
            value("numeroCuotas").toInt(), value("fechaSolicitud"),
            MUtils.siguienteMesConDia(value("fechaSolicitud"), value("diaPago").toInt()),
            if (insurance.signum() == 0) emptyList() else listOf(Seguro.Porcentaje("Seguro 01", insurance.divide(BigDecimal("100")))),
            if (value("annualBase") == "360") GeneradorPlanPagos.BaseAnual.BASE_360 else GeneradorPlanPagos.BaseAnual.BASE_365,
            if (type == "03") GeneradorPlanPagos.TipoDias.PERIOD else GeneradorPlanPagos.TipoDias.REAL,
            type == "01", value("period").toInt()
        )
    }

    fun intent(context: Context) = Intent(context, CreditSimulatorDetailActivity::class.java).also { intent ->
        inputs.forEach { (key, value) -> intent.putExtra(key, value) }
    }
}

class SimulationHistory(context: Context) {
    private val preferences = context.getSharedPreferences("simulation_history", Context.MODE_PRIVATE)

    fun all(): List<SavedSimulation> {
        val array = JSONArray(preferences.getString("records", "[]"))
        return (0 until array.length()).map { index ->
            val json = array.getJSONObject(index)
            val inputs = json.getJSONObject("inputs")
            SavedSimulation(json.getString("id"), json.getLong("updatedAt"),
                inputs.keys().asSequence().associateWith { inputs.getString(it) })
        }.sortedByDescending { it.updatedAt }
    }

    fun save(record: SavedSimulation) = write(all().filterNot { it.id == record.id } + record)
    fun delete(id: String) = write(all().filterNot { it.id == id })

    private fun write(records: List<SavedSimulation>): Boolean {
        val array = JSONArray()
        records.forEach { record ->
            array.put(JSONObject().put("id", record.id).put("updatedAt", record.updatedAt)
                .put("inputs", JSONObject(record.inputs)))
        }
        return preferences.edit().putString("records", array.toString()).commit()
    }
}
